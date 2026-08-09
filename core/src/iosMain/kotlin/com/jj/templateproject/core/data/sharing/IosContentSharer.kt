package com.jj.templateproject.core.data.sharing

import com.jj.templateproject.domain.sharing.ContentSharer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIActivityItemSourceProtocol
import platform.UIKit.UIActivityType
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController
import platform.darwin.NSObject

/**
 * Hands text to iOS's share sheet.
 *
 * Presented from the topmost view controller rather than the root one: presenting from a controller
 * that already has something presented over it is a no-op with a console warning, so a share
 * triggered from a modal would silently do nothing.
 *
 * With no window to present from there is nothing to do and nothing to report, so this returns
 * quietly, matching the Android implementation's behaviour when no app can receive the text: a
 * share that cannot happen is not worth taking the caller down for.
 */
class IosContentSharer : ContentSharer {

    override fun shareText(text: String, subject: String?) {
        val presenter = topmostViewController() ?: return
        // A bare string is enough when there is no subject; the item source below exists only to
        // carry one.
        val item: Any = subject?.let { SubjectedText(text = text, subject = it) } ?: text
        val controller = UIActivityViewController(activityItems = listOf(item), applicationActivities = null)
        anchorForPopover(controller, presenter)
        presenter.presentViewController(controller, animated = true, completion = null)
    }

    /**
     * On iPad the sheet is a popover, and UIKit raises rather than guesses if it is given no anchor.
     * Anchored to the middle of the presenting view: there is no originating control to point at
     * here, since this seam is reached from a domain call rather than from a tapped view.
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun anchorForPopover(controller: UIActivityViewController, presenter: UIViewController) {
        val popover = controller.popoverPresentationController ?: return
        popover.sourceView = presenter.view
        popover.sourceRect = presenter.view.bounds.useContents {
            CGRectMake(x = size.width / 2, y = size.height / 2, width = 0.0, height = 0.0)
        }
    }

    private fun topmostViewController(): UIViewController? {
        // keyWindow is deprecated for multi-scene apps, where "the" key window is ambiguous. Kept
        // because this template is single-scene, and because the replacement means walking
        // connectedScenes for an active one, which answers the same question with more code and the
        // same nullability.
        var controller = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return null
        while (true) {
            controller = controller.presentedViewController ?: return controller
        }
    }
}

/**
 * Text that also offers a subject, for the few activities that have one (mail, and little else).
 *
 * A `UIActivityItemSource` rather than the widely copied `setValue(subject, forKey: "subject")`:
 * that reaches through key-value coding into a property UIKit does not declare, which is the sort
 * of thing that works until an iOS release quietly removes it. This is the API Apple documents for
 * the purpose.
 */
private class SubjectedText(
    private val text: String,
    private val subject: String,
) : NSObject(), UIActivityItemSourceProtocol {

    // Shown while the sheet works out what it is sharing; the real value costs nothing to produce,
    // so it doubles as its own placeholder.
    override fun activityViewControllerPlaceholderItem(activityViewController: UIActivityViewController): Any = text

    @ObjCSignatureOverride
    override fun activityViewController(
        activityViewController: UIActivityViewController,
        itemForActivityType: UIActivityType,
    ): Any = text

    @ObjCSignatureOverride
    override fun activityViewController(
        activityViewController: UIActivityViewController,
        subjectForActivityType: UIActivityType,
    ): String = subject
}
