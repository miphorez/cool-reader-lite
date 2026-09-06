const fs = require('fs');
const path = require('path');

const [auditDir, rowNumber, iconId, iconName, previewFilename] = process.argv.slice(2);
if (!auditDir || !rowNumber || !iconId || !iconName || !previewFilename)
  throw new Error('Usage: set_audit_candidate.js <audit-dir> <row-number> <icon-id> <icon-name> <preview-filename>');

const htmlPath = path.join(auditDir, '05_Аудит_иконок_Cool_Reader_Lite.html');
const markdownPath = path.join(auditDir, '05_Аудит_иконок_Cool_Reader_Lite.md');
const previewPath = path.join(auditDir, 'Доказательства', 'Иконки', 'new', previewFilename);
const previewBase64 = fs.readFileSync(previewPath).toString('base64');
const label = `icon:${iconId}:${iconName}`;
const htmlCandidate = `<td class="preview-cell new-cell"><img src="data:image/png;base64,${previewBase64}" width="56" alt="${label}"> <br><small><code>${label}</code><br>Однозначное соответствие</small></td>`;
const markdownCandidate = `<img src="Доказательства/Иконки/new/${previewFilename}" width="56" alt="${label}"> <br><small>\`${label}\`<br>Однозначное соответствие</small>`;

let html = fs.readFileSync(htmlPath, 'utf8');
const rowExpression = new RegExp(`<tr id="row-${rowNumber}"[\\s\\S]*?</tr>`);
const rowMatch = html.match(rowExpression);
if (!rowMatch) throw new Error(`HTML row ${rowNumber} not found`);
let htmlRow = rowMatch[0]
  .replace('data-status="missing"', 'data-status="applied"')
  .replace(/data-search="([^"]*)"/, (_, content) => {
    const cleanContent = content.replace(/\s+icon:\d+:[^\s"]+\s+однозначное соответствие/g, '').trim();
    return `data-search="${cleanContent} ${label} однозначное соответствие"`;
  })
  .replace('<span class="status status-missing">Нет соответствия</span>', '<span class="status status-applied">Применено</span>')
  .replace(/<td class="preview-cell new-cell">[\s\S]*?<\/td>/, htmlCandidate);
html = html.replace(rowExpression, htmlRow);
fs.writeFileSync(htmlPath, html);

let markdown = fs.readFileSync(markdownPath, 'utf8');
const lines = markdown.split(/\r?\n/);
const markdownRow = lines.findIndex(line => line.startsWith(`| **№ ${rowNumber} ·`));
if (markdownRow < 0) throw new Error(`Markdown row ${rowNumber} not found`);
const cells = lines[markdownRow].split(' | ');
if (cells.length !== 3) throw new Error(`Unexpected Markdown structure in row ${rowNumber}`);
cells[2] = `${markdownCandidate} |`;
lines[markdownRow] = cells.join(' | ');
fs.writeFileSync(markdownPath, `${lines.join('\n')}\n`);

console.log(JSON.stringify({ htmlPath, markdownPath, rowNumber, label, previewPath }, null, 2));
