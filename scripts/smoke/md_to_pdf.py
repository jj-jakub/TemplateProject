#!/usr/bin/env python3
"""Render a Markdown document to PDF, images and all.

Used by the release smoke test (SmokeTestRunInput.md) to turn its report, screenshots included,
into something that can be handed to someone who will not clone the repository. Deliberately
dependency-free, because a release tool that needs a pip install is a release tool that stops
working: the Markdown subset is handled here and headless Chrome does the typesetting.

    python3 md_to_pdf.py report.md report.pdf --title "TemplateProject 0.1 (1)"

Relative image paths resolve against the Markdown file's own directory, so a document written
beside the screenshots just references them by filename.

Supported: headings, paragraphs, unordered lists, ordered lists, pipe tables, fenced code,
images, bold, inline code, links, and `---` rules. That is what the smoke documents use.
"""
import argparse
import html
import pathlib
import re
import shutil
import subprocess
import sys

CHROME_CANDIDATES = [
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
    "/Applications/Chromium.app/Contents/MacOS/Chromium",
    "/usr/bin/google-chrome",
    "/usr/bin/chromium",
]

STYLE = """
@page { size: A4; margin: 18mm 16mm; }
body { font: 10.5pt/1.55 -apple-system, "Helvetica Neue", Arial, sans-serif; color: #16151a; }
h1 { font-size: 22pt; margin: 0 0 4pt; letter-spacing: -0.3pt; }
h2 { font-size: 14pt; margin: 22pt 0 6pt; padding-bottom: 4pt; border-bottom: 1px solid #d8d5e0;
     page-break-after: avoid; }
h3 { font-size: 11.5pt; margin: 14pt 0 4pt; page-break-after: avoid; }
p, li { margin: 0 0 6pt; }
ul, ol { margin: 0 0 8pt; padding-left: 18pt; }
code { font: 9pt/1.4 "SF Mono", Menlo, monospace; background: #f1eff5; padding: 1pt 3pt;
       border-radius: 3px; }
pre { font: 8.6pt/1.45 "SF Mono", Menlo, monospace; background: #f6f5f9; border: 1px solid #e3e0ea;
      border-radius: 5px; padding: 8pt 10pt; overflow-wrap: break-word; white-space: pre-wrap;
      page-break-inside: avoid; }
pre code { background: none; padding: 0; }
table { border-collapse: collapse; width: 100%; margin: 0 0 10pt; font-size: 9.4pt;
        page-break-inside: avoid; }
th { text-align: left; background: #f1eff5; }
th, td { border: 1px solid #ddd9e4; padding: 4pt 6pt; vertical-align: top; }
hr { border: none; border-top: 1px solid #d8d5e0; margin: 16pt 0; }
img { max-width: 100%; }
.shots { display: flex; flex-wrap: wrap; gap: 8pt; page-break-inside: avoid; }
.shot { width: 31%; text-align: center; font-size: 8pt; color: #57525f; }
.shot img { width: 100%; border: 1px solid #ddd9e4; border-radius: 4px; }
.lede { color: #57525f; margin: 0 0 14pt; }
blockquote { margin: 0 0 8pt; padding: 6pt 10pt; background: #f6f5f9; border-left: 3px solid #b9b2c7; }
"""

INLINE = [
    (re.compile(r"`([^`]+)`"), lambda m: f"<code>{html.escape(m.group(1))}</code>"),
    (re.compile(r"!\[([^\]]*)\]\(([^)]+)\)"), lambda m: f'<img alt="{html.escape(m.group(1))}" src="{m.group(2)}">'),
    (re.compile(r"\[([^\]]+)\]\(([^)]+)\)"), lambda m: f'<a href="{m.group(2)}">{m.group(1)}</a>'),
    (re.compile(r"\*\*([^*]+)\*\*"), lambda m: f"<strong>{m.group(1)}</strong>"),
]


def inline(text: str) -> str:
    # Escape first, then re-introduce only the markup we recognise, so a stray < in prose is safe.
    out = html.escape(text)
    out = out.replace("&lt;br&gt;", "<br>")
    for pattern, repl in INLINE:
        out = pattern.sub(repl, out)
    return out


def render(markdown: str) -> str:
    lines = markdown.splitlines()
    out: list[str] = []
    i = 0
    while i < len(lines):
        line = lines[i]
        if line.startswith("```"):
            block, i = [], i + 1
            while i < len(lines) and not lines[i].startswith("```"):
                block.append(lines[i])
                i += 1
            out.append(f"<pre><code>{html.escape(chr(10).join(block))}</code></pre>")
            i += 1
        elif line.startswith("|"):
            rows, i = [], i
            while i < len(lines) and lines[i].startswith("|"):
                rows.append(lines[i])
                i += 1
            out.append(table(rows))
        elif re.match(r"^#{1,4} ", line):
            level = len(line) - len(line.lstrip("#"))
            out.append(f"<h{level}>{inline(line[level:].strip())}</h{level}>")
            i += 1
        elif re.match(r"^[-*] ", line):
            items, i = [], i
            while i < len(lines) and re.match(r"^[-*] ", lines[i]):
                items.append(f"<li>{inline(lines[i][2:])}</li>")
                i += 1
            out.append("<ul>" + "".join(items) + "</ul>")
        elif re.match(r"^\d+\. ", line):
            items, i = [], i
            while i < len(lines) and re.match(r"^\d+\. ", lines[i]):
                items.append(f"<li>{inline(re.sub(r'^\\d+\\. ', '', lines[i]))}</li>")
                i += 1
            out.append("<ol>" + "".join(items) + "</ol>")
        elif line.startswith("> "):
            out.append(f"<blockquote>{inline(line[2:])}</blockquote>")
            i += 1
        elif line.strip() == "---":
            out.append("<hr>")
            i += 1
        elif line.strip().startswith("<"):
            out.append(line)
            i += 1
        elif line.strip():
            para, i = [], i
            while i < len(lines) and lines[i].strip() and not re.match(r"^(#{1,4} |[-*] |\d+\. |\||```|> |<)", lines[i]):
                para.append(lines[i])
                i += 1
            out.append(f"<p>{inline(' '.join(para))}</p>")
        else:
            i += 1
    return "\n".join(out)


def table(rows: list[str]) -> str:
    cells = [[c.strip() for c in r.strip().strip("|").split("|")] for r in rows]
    body = [r for r in cells[1:] if not all(set(c) <= set("-: ") for c in r)]
    head = "".join(f"<th>{inline(c)}</th>" for c in cells[0])
    out = [f"<table><thead><tr>{head}</tr></thead><tbody>"]
    for row in body:
        out.append("<tr>" + "".join(f"<td>{inline(c)}</td>" for c in row) + "</tr>")
    return "".join(out) + "</tbody></table>"


def shrink_images(body: str, base: pathlib.Path, width: int) -> str:
    """Point every <img> at a downscaled copy.

    Chrome embeds an image at its native resolution however small the CSS draws it, so a document
    illustrated with phone screenshots comes out at tens of megabytes. `sips` ships with macOS, so
    this costs no dependency; where it is missing the images are left alone and the PDF is merely
    large.
    """
    if not shutil.which("sips"):
        return body
    cache = base / ".pdf-thumbs"
    cache.mkdir(exist_ok=True)

    def swap(match: re.Match) -> str:
        src = match.group(1)
        original = base / src
        if not original.exists():
            return match.group(0)
        thumb = cache / original.name
        if not thumb.exists():
            subprocess.run(["sips", "-Z", str(width), str(original), "--out", str(thumb)],
                           check=False, capture_output=True)
        return match.group(0).replace(src, thumb.relative_to(base).as_posix())

    return re.sub(r'src="([^"]+)"', swap, body)


def chrome() -> str:
    for path in CHROME_CANDIDATES:
        if pathlib.Path(path).exists():
            return path
    sys.exit("no Chrome or Chromium found; install one or edit CHROME_CANDIDATES")


def main() -> int:
    p = argparse.ArgumentParser()
    p.add_argument("source")
    p.add_argument("output")
    p.add_argument("--title", default="")
    p.add_argument("--max-image-width", type=int, default=760,
                   help="downscale embedded images to this pixel width (0 keeps originals)")
    a = p.parse_args()

    source = pathlib.Path(a.source).resolve()
    body = render(source.read_text())
    if a.max_image_width:
        body = shrink_images(body, source.parent, a.max_image_width)
    document = f"""<!doctype html><html><head><meta charset="utf-8">
<title>{html.escape(a.title or source.stem)}</title><style>{STYLE}</style></head>
<body>{body}</body></html>"""
    page = source.with_suffix(".rendered.html")
    page.write_text(document)

    subprocess.run(
        [chrome(), "--headless", "--disable-gpu", "--no-pdf-header-footer",
         f"--print-to-pdf={pathlib.Path(a.output).resolve()}", page.as_uri()],
        check=True, capture_output=True,
    )
    page.unlink()
    print(f"wrote {a.output}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
