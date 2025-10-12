import "./PieChart.css";

interface PieChartProps extends React.SVGProps<SVGSVGElement> {
    sectors: Map<string, number>;
    radius: number;
}

interface CircleSectorProps extends React.SVGProps<SVGPathElement> {
    radius: number;
    offset: number;
    degree: number;
}

const CircleSector = ({radius, offset, degree, ...rest}: CircleSectorProps) => {
    const start = {
        x: Math.cos(offset) * radius,
        y: Math.sin(offset) * radius
    };
    const end = {
        x: (radius + (Math.cos(degree + offset) * radius)).toFixed(10),
        y: (radius + (Math.sin(degree + offset) * radius)).toFixed(10)
    };
    const biggerThanHalf = degree > Math.PI ? 1 : 0;

    const d = `
        M${radius} ${radius} 
        m${start.x} ${start.y} 
        A${radius} ${radius} 0 ${biggerThanHalf} 1 ${end.x} ${end.y}
        L${radius} ${radius}
        Z`;
    
    return (<path
        d={d}
        {...rest}
    />);
}


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

export const PieChart = ({sectors, radius, ...rest}: PieChartProps) => {
    interface sectorProps {
        key: string;
        value: number;
        offset: number;
        angle: number;
        color: string;
    }

    const total = sectors.values().reduce((acc, val) => acc + val, 0);
    const sectorAnglesOffsets: sectorProps[] = [];
    let prevOffset = 0;
    for (const [key, value] of sectors.entries()) {
        const angle = (value / total) * 2 * Math.PI;
        sectorAnglesOffsets.push({
            key: key,
            value: value,
            offset: prevOffset,
            angle: angle,
            color: stringToColor(key)
        });
        prevOffset += angle;
    }

    return (<div className="pie-chart">
        <svg height={(radius * 2)} width={(radius * 2)} xmlns="http://www.w3.org/2000.svg" {...rest}>
            { 
                sectorAnglesOffsets.map(({key, offset, angle, color}) => 
                    <CircleSector key={key}
                        fill={color} 
                        cx={radius} cy={radius} radius={radius} 
                        offset={offset} degree={angle}/> ) 
            }
        </svg>
        <div className="legend" style={{width: `${radius*2}px`}}>
            { 
                sectorAnglesOffsets.map(({key, color}) => 
                    <div key={key}>
                        <div style={{backgroundColor: color}}/>
                        <span>{key}</span>
                    </div>
                )
            }
        </div>
    </div>)
}