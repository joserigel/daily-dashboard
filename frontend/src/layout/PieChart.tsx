import "./PieChart.css";

interface PieChartProps extends React.SVGProps<SVGSVGElement> {
    sectors: Map<string, number>;
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

    console.log(d)
    
    return (<path
        d={d}
        {...rest}
    />);
}



export const PieChart = ({sectors, ...rest}: PieChartProps) => {
    const total = sectors.values().reduce((acc, val) => acc + val, 0);

    const radius = 50

    return (<svg height={100} width={100} xmlns="http://www.w3.org/2000.svg" className="pie-chart" {...rest}>
        <CircleSector stroke="#ff0000" fill="red" cx={50} cy={50} radius={radius} offset={Math.PI * 0.8} degree={Math.PI * (1.55)}/>
    </svg>)
}