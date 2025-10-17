import { useState } from "react";
import "./PieChart.css";

interface PieChartProps extends React.SVGProps<SVGSVGElement> {
    sectors: Map<string, number>;
    radius: number;
}

interface CircleSectorProps extends React.SVGProps<SVGPathElement> {
    radius: number;
    offset: number;
    angle: number;
}

const CircleSector = ({radius, offset, angle, ...rest}: CircleSectorProps) => {
    const start = {
        x: Math.cos(offset) * radius,
        y: Math.sin(offset) * radius
    };
    const end = {
        x: (radius + (Math.cos(angle + offset) * radius)).toFixed(10),
        y: (radius + (Math.sin(angle + offset) * radius)).toFixed(10)
    };
    const biggerThanHalf = angle > Math.PI ? 1 : 0;

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
    const boxSize = {
        width: 80,
        height: 45
    };
    const boxOffset = radius * 0.75;
    const [hover, setHover] = useState<string>(undefined);
    interface sectorProps {
        key: string;
        value: number;
        percentage: number;
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
            percentage: (value / total) * 100,
            offset: prevOffset,
            angle: angle,
            color: stringToColor(key)
        });
        prevOffset += angle;
    }

    const highlightedSector = sectorAnglesOffsets.find((x) => x.key === hover);
    const boxPosition = {
        x: radius - (boxSize.width / 2) 
            + Math.cos(
                (highlightedSector?.offset ?? 0) + 
                (highlightedSector?.angle ?? 0) / 2
            ) * boxOffset,
        y: radius - (boxSize.height / 2) 
            + Math.sin(
                (highlightedSector?.offset ?? 0) +
                (highlightedSector?.angle ?? 0) / 2
            ) * boxOffset
    }

    return (<div className="pie-chart"
        onMouseLeave={() => setHover(undefined)}>
        {
            hover &&
            <div className="info-box"
                style={{
                    left: boxPosition.x,
                    top: boxPosition.y,
                    width: `${boxSize.width}px`,
                    height: `${boxSize.height}px`
                }}>
                <h1>{ hover }</h1>
                <h3>€{ highlightedSector?.value }</h3>
                <h3>{ highlightedSector.percentage.toFixed(2) }%</h3>
            </div>
        }
        <svg height={(radius * 2)} width={(radius * 2)} 
            xmlns="http://www.w3.org/2000.svg" {...rest}>
            { 
                sectorAnglesOffsets.map(({key, offset, angle, color}) => 
                    <CircleSector key={key}
                        onMouseOver={() => setHover(key)}
                        fill={color}
                        stroke="white"
                        strokeWidth={hover === key ? "1px" : "0px"}
                        cx={radius} cy={radius} radius={radius} 
                        offset={offset} angle={angle}/> ) 
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