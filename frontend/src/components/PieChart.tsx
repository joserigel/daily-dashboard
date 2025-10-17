import { useState } from "react";
import "./PieChart.css";
import { stringToColor } from "../utils/stringToColor";

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
                sectorAnglesOffsets.length > 1 ? 
                sectorAnglesOffsets.map(({key, offset, angle, color}) => 
                    <CircleSector key={key}
                        onMouseOver={() => setHover(key)}
                        fill={color}
                        stroke="white"
                        strokeWidth={hover === key ? "1px" : "0px"}
                        cx={radius} cy={radius} radius={radius} 
                        offset={offset} angle={angle}/> ) :
                sectorAnglesOffsets.length > 0 &&
                    <circle
                        onMouseOver={() => setHover(sectorAnglesOffsets[0].key)}
                        cx={radius} cy={radius}
                        stroke="white"
                        r={radius}
                        strokeWidth={hover ? "1px" : "0px"}
                        fill={sectorAnglesOffsets[0].color}
                    />
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