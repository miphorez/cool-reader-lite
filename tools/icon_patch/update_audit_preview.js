const fs = require('fs');

const [htmlPath, rowNumber, previewPath] = process.argv.slice(2);
if (!htmlPath || !rowNumber || !previewPath)
  throw new Error('Usage: update_audit_preview.js <html-path> <row-number> <preview-path>');

const previewBase64 = fs.readFileSync(previewPath).toString('base64');
let html = fs.readFileSync(htmlPath, 'utf8');
const rowExpression = new RegExp(`(<tr id="row-${rowNumber}"[\\s\\S]*?<td class="preview-cell new-cell"><img src="data:image/png;base64,)([^"]+)("[\\s\\S]*?</td>[\\s\\S]*?</tr>)`);
if (!rowExpression.test(html)) throw new Error(`HTML row ${rowNumber} with an embedded preview was not found`);
html = html.replace(rowExpression, `$1${previewBase64}$3`);
fs.writeFileSync(htmlPath, html);

console.log(JSON.stringify({ htmlPath, rowNumber, previewPath }, null, 2));
