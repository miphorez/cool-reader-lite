const fs = require('fs');
const path = require('path');

const repoRoot = path.resolve(__dirname, '..', '..');
const outputDir = path.join(repoRoot, 'icons_src', 'figma_book_icons', 'formats');
const formats = [
  { id: '005', name: 'book_open_chm', letter: 'C' },
  { id: '006', name: 'book_open_doc', letter: 'D' },
  { id: '007', name: 'book_open_epub', letter: 'E' },
  { id: '008', name: 'book_open_fb2', letter: 'F', exponent: '2' },
  { id: '009', name: 'book_open_fb3', letter: 'F', exponent: '3' },
  { id: '010', name: 'book_open_html', letter: 'H' },
  { id: '011', name: 'book_open_odt', letter: 'O' },
  { id: '012', name: 'book_open_pdb', letter: 'P' },
  { id: '013', name: 'book_open_rtf', letter: 'R' },
  { id: '014', name: 'book_open_txt', letter: 'T' },
];

const mainPage = 'M15.5 3.575H7.25C5.8 3.575 5.075 4.3 5.075 5.75V17.25C5.075 16.05 6.05 15.075 7.25 15.075H15.5V3.575Z';
const backCover = 'M18.925 3.575V20.425H7.25C6.05 20.425 5.075 19.45 5.075 18.25V17.25';

const genericSvg = `<?xml version="1.0" encoding="utf-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="72" height="72" viewBox="0 0 24 24" fill="none">
  <path d="${mainPage}" stroke="white" stroke-width="1.15" stroke-linecap="round" stroke-linejoin="round"/>
  <path d="${backCover}" stroke="white" stroke-width="1.15" stroke-linecap="round" stroke-linejoin="round"/>
</svg>
`;
fs.writeFileSync(path.join(outputDir, '003_book_format_generic.svg'), genericSvg, 'utf8');

for (const format of formats) {
  const mainX = format.exponent ? '9.5' : '10.25';
  const exponent = format.exponent
    ? `\n  <text x="13" y="8.5" text-anchor="middle" class="exponent">${format.exponent}</text>`
    : '';
  const svg = `<?xml version="1.0" encoding="utf-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="72" height="72" viewBox="0 0 24 24" fill="none">
  <path d="${mainPage}" stroke="white" stroke-width="1.15" stroke-linecap="round" stroke-linejoin="round"/>
  <path d="${backCover}" stroke="white" stroke-width="1.15" stroke-linecap="round" stroke-linejoin="round"/>
  <style>
    .main { font-family: "Segoe UI", Arial, sans-serif; font-size: 10px; font-weight: 700; fill: white; }
    .exponent { font-family: "Segoe UI", Arial, sans-serif; font-size: 4px; font-weight: 700; fill: white; }
  </style>
  <text x="${mainX}" y="12.5" text-anchor="middle" class="main">${format.letter}</text>${exponent}
</svg>
`;
  fs.writeFileSync(path.join(outputDir, `${format.id}_${format.name}.svg`), svg, 'utf8');
}

console.log(JSON.stringify({ generated: formats.length + 1, base: 'book_6_material' }));
