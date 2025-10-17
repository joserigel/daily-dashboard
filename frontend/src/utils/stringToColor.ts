export const stringToColor = (key: string): string => {
    
    const seed = key.split("").reduce((acc, val) => acc + val.charCodeAt(0), 0);
    const hue = seed % 360;
    const saturation = 1;
    const value = 1;

    const C = saturation * value;
    const X = C * (1 - Math.abs((hue / 60) % 2 - 1));

    const m = value - C;

    let r = 0; let g = 0; let b = 0;
    if (hue < 60) {
        r = C; g = X; b = 0;
     } else if (hue < 120) {
        r = X; g = C; b = 0;
    } else if (hue < 180) {
        r = 0; g = C; b = X;
    } else if (hue < 240) {
        r = 0; g = X; b = C;
    } else if (hue < 300) {
        r = X; g = 0; b = C;
    } else {
        r = C; g = 0; b = X;
    }

    const R = Math.round((r + m) * 255);
    const G = Math.round((g + m) * 255);
    const B = Math.round((b + m) * 255);

    const toHex = (x: number) => {
        return ('0' + x.toString(16)).slice(-2);
    }


    return `#${toHex(R)}${toHex(G)}${toHex(B)}`
}