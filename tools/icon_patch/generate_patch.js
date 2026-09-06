const fs = require('fs');
const path = require('path');
const sharp = require('sharp');

const repoRoot = path.resolve(__dirname, '..', '..');
const resRoot = path.join(repoRoot, 'android', 'res');
const config = JSON.parse(fs.readFileSync(path.join(__dirname, 'icon_mapping.json'), 'utf8'));
const libraryRoots = {
  figma_interface_icons: path.join(repoRoot, 'icons_src', 'figma_interface_icons'),
  figma_book_icons: path.join(repoRoot, 'icons_src', 'figma_book_icons'),
  figma_color_picker_icons: path.join(repoRoot, 'icons_src', 'figma_color_picker_icons'),
};
const manifestsByCollection = new Map(Object.entries(libraryRoots).map(([collection, root]) => {
  const manifest = JSON.parse(fs.readFileSync(path.join(root, 'manifest.json'), 'utf8'));
  return [collection, new Map(manifest.map(item => [item.label.split(':')[1], item]))];
}));
const baseColor = '#808080';
const checkboxDensities = {
  ldpi: 24,
  mdpi: 32,
  hdpi: 48,
  xhdpi: 64,
  xxhdpi: 96,
};

async function writeFileWithRetry(target, data) {
  for (let attempt = 0; ; attempt++) {
    try {
      fs.writeFileSync(target, data);
      return;
    } catch (error) {
      if (attempt >= 19 || !['UNKNOWN', 'EBUSY', 'EACCES', 'EPERM'].includes(error.code)) throw error;
      await new Promise(resolve => setTimeout(resolve, 50 * (attempt + 1)));
    }
  }
}

function walk(dir) {
  return fs.readdirSync(dir, { withFileTypes: true }).flatMap(entry => {
    const full = path.join(dir, entry.name);
    return entry.isDirectory() ? walk(full) : [full];
  });
}

function targetsFor(group, mapping, files) {
  if (mapping.densitySizes) {
    return Object.keys(mapping.densitySizes).map(density =>
      path.join(resRoot, `drawable-${density}`, `${group}.png`));
  }
  const names = new Set([group, `${group}_hc`]);
  for (const variant of mapping.variants || []) names.add(`${group}_${variant}`);
  const targets = files.filter(file => names.has(path.parse(file).name) && file.toLowerCase().endsWith('.png'));
  if (group === 'ic_menu_preferences' && targets.length === 0)
    targets.push(path.join(resRoot, 'drawable', 'ic_menu_preferences.png'));
  if (mapping.createTarget && targets.length === 0)
    targets.push(path.join(resRoot, 'drawable', `${group}.png`));
  return targets;
}

async function targetSize(target, mapping) {
  const density = path.basename(path.dirname(target)).replace('drawable-', '');
  if (mapping.densitySizes?.[density]) {
    const size = mapping.densitySizes[density];
    return { width: size, height: size };
  }
  if (fs.existsSync(target)) {
    const metadata = await sharp(target).metadata();
    return { width: metadata.width, height: metadata.height };
  }
  return { width: 48, height: 48 };
}

function checkboxStateColor(resourceName) {
  if (resourceName.includes('_disabled')) return '#808080';
  return resourceName.endsWith('_hc_dark') ? '#ffffff' : '#000000';
}

async function renderCheckboxBitmap(svg, color, canvasSize, target) {
  const iconSize = Math.round(canvasSize * 0.75);
  const offset = Math.floor((canvasSize - iconSize) / 2);
  const icon = await sharp(Buffer.from(svg.replaceAll('white', color)))
    .resize(iconSize, iconSize, { fit: 'contain' })
    .png()
    .toBuffer();
  const png = await sharp({
    create: { width: canvasSize, height: canvasSize, channels: 4, background: '#00000000' },
  })
    .composite([{ input: icon, left: offset, top: offset }])
    .png()
    .toBuffer();
  await writeFileWithRetry(target, png);
}

async function generateCheckboxResources(mapping, source, resourceFiles) {
  const uncheckedSource = path.join(libraryRoots.figma_interface_icons, mapping.uncheckedFile);
  if (!fs.existsSync(uncheckedSource)) throw new Error(`Missing unchecked checkbox source ${uncheckedSource}`);
  const checkedSvg = fs.readFileSync(source, 'utf8');
  const uncheckedSvg = fs.readFileSync(uncheckedSource, 'utf8');
  const stateFiles = resourceFiles.filter(file => {
    const name = path.parse(file).name;
    return file.toLowerCase().endsWith('.xml') && /^btn_check_(?:on|off)_/.test(name);
  });
  let generated = 0;

  for (const stateFile of stateFiles) {
    const resourceName = path.parse(stateFile).name;
    const stateSvg = resourceName.startsWith('btn_check_on_') ? checkedSvg : uncheckedSvg;
    const color = checkboxStateColor(resourceName);
    const bitmapName = `${resourceName}_bitmap`;
    for (const [density, canvasSize] of Object.entries(checkboxDensities)) {
      const targetDir = path.join(resRoot, `drawable-${density}`);
      fs.mkdirSync(targetDir, { recursive: true });
      await renderCheckboxBitmap(stateSvg, color, canvasSize, path.join(targetDir, `${bitmapName}.png`));
      generated++;
    }
    await writeFileWithRetry(stateFile,
      `<?xml version="1.0" encoding="utf-8"?>\n` +
      `<bitmap xmlns:android="http://schemas.android.com/apk/res/android"\n` +
      `    android:src="@drawable/${bitmapName}"\n` +
      `    android:gravity="center" />\n`);
  }

  return { generated, aliases: stateFiles.length };
}

async function generateRadioResources(mapping, source, resourceFiles) {
  const uncheckedSource = path.join(libraryRoots.figma_interface_icons, mapping.uncheckedFile);
  if (!fs.existsSync(uncheckedSource)) throw new Error(`Missing unchecked radio source ${uncheckedSource}`);
  const checkedSvg = fs.readFileSync(source, 'utf8');
  const uncheckedSvg = fs.readFileSync(uncheckedSource, 'utf8');
  const stateFiles = resourceFiles.filter(file => {
    const name = path.parse(file).name;
    return file.toLowerCase().endsWith('.xml') && /^btn_radio_(?:on|off)_/.test(name);
  });
  let generated = 0;

  for (const stateFile of stateFiles) {
    const resourceName = path.parse(stateFile).name;
    const stateSvg = resourceName.startsWith('btn_radio_on_') ? checkedSvg : uncheckedSvg;
    const color = checkboxStateColor(resourceName);
    const bitmapName = `${resourceName}_bitmap`;
    for (const [density, canvasSize] of Object.entries(checkboxDensities)) {
      const targetDir = path.join(resRoot, `drawable-${density}`);
      fs.mkdirSync(targetDir, { recursive: true });
      await renderCheckboxBitmap(stateSvg, color, canvasSize, path.join(targetDir, `${bitmapName}.png`));
      generated++;
    }
    await writeFileWithRetry(stateFile,
      `<?xml version="1.0" encoding="utf-8"?>\n` +
      `<bitmap xmlns:android="http://schemas.android.com/apk/res/android"\n` +
      `    android:src="@drawable/${bitmapName}"\n` +
      `    android:gravity="center" />\n`);
  }

  return { generated, aliases: stateFiles.length };
}

async function main() {
  const resourceFiles = walk(resRoot);
  const onlyGroup = process.argv[2];
  let generated = 0;
  let generatedXmlAliases = 0;
  for (const [group, mapping] of Object.entries(config.mappings)) {
    if (onlyGroup && group !== onlyGroup) continue;
    if (!mapping.approved || mapping.candidates.length !== 1) continue;
    const collection = mapping.collection || 'figma_interface_icons';
    const libraryRoot = libraryRoots[collection];
    const sourceItem = manifestsByCollection.get(collection)?.get(mapping.candidates[0]);
    if (!libraryRoot) throw new Error(`Unknown icon collection ${collection} for ${group}`);
    if (!sourceItem) throw new Error(`Missing source icon ${mapping.candidates[0]} for ${group}`);
    const source = path.join(libraryRoot, sourceItem.file);
		if (group === 'checkbox_control') {
			const result = await generateCheckboxResources(mapping, source, resourceFiles);
			generated += result.generated;
			generatedXmlAliases += result.aliases;
			continue;
		}
		if (group === 'radio_control') {
			const result = await generateRadioResources(mapping, source, resourceFiles);
			generated += result.generated;
			generatedXmlAliases += result.aliases;
			continue;
		}
		const svg = fs.readFileSync(source, 'utf8').replaceAll('white', baseColor);
    for (const target of targetsFor(group, mapping, resourceFiles)) {
      const size = await targetSize(target, mapping);
      const png = await sharp(Buffer.from(svg))
        .resize(size.width, size.height, { fit: 'contain' })
        .png()
        .toBuffer();
      await writeFileWithRetry(target, png);
      generated++;
    }
  }
  console.log(JSON.stringify({ generated, generatedXmlAliases, baseColor }, null, 2));
}

main().catch(error => {
  console.error(error);
  process.exitCode = 1;
});
