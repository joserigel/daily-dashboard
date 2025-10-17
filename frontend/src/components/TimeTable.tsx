import { stringToColor } from "../utils/stringToColor";
import { Schedule } from "../models/Schedule"
import "./TimeTable.css"

interface TimeTableProps extends React.DetailedHTMLProps<React.HTMLAttributes<HTMLDivElement>, HTMLDivElement> {
    schedules: Schedule[];
}

export const TimeTable = ({schedules, ...rest}: TimeTableProps) => {
    const convertHeight = (schedule: Schedule) => {
        const durationHour = parseInt(schedule.end.split(":")[0]) - parseInt(schedule.start.split(":")[0]);
        const durationMinute = parseInt(schedule.end.split(":")[1]) - parseInt(schedule.start.split(":")[1]);
        const durationSecond = parseInt(schedule.end.split(":")[2]) - parseInt(schedule.start.split(":")[2]);

        const totalDuration = durationHour * 60 * 60 + durationMinute * 60 + durationSecond;
        const height = `${(totalDuration / 1500).toFixed(2)}em`;
        return height;
    }
    return (<div className="time-table" {...rest}>
        {
            schedules.map((schedule) => <div key={schedule.id} 
                style={{
                    borderColor: stringToColor(schedule.description)
                }}>
                <h1>{ schedule.description}</h1>
                <h3 style={{height: convertHeight(schedule)}}>{ schedule.start } - { schedule.end }</h3>
            </div>)
        }
    </div>)
}