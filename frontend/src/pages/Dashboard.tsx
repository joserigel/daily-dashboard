import "./Dashboard.css";
import { useEffect, useState } from "react"
import { PieChart } from "../components/PieChart"
import axios from "axios";
import { TimeTable } from "../components/TimeTable";
import { Schedule } from "../models/Schedule";
import { Menu } from "../components/Menu";
import { DayMenu } from "../models/MenuItem";
import { Expense } from "../models/Expense";

const Dashboard = () => {
    const [values, setValues] = useState<Map<string, number>>(new Map<string, number>());
    const [schedules, setSchedules] = useState<Schedule[]>([]);
    const [expenseInput, setExpenseInput] = useState<string>("");
    const [dayMenu, setDayMenu] = useState<DayMenu>(undefined);
    const dayOfWeek = new Date().toLocaleString('en-us', {  weekday: 'long' }).toUpperCase();

    useEffect(() => {
        axios.get("/expenses-per-category", {
            params: {
                start: new Date().toISOString().slice(0, 8) + "01",
                end: new Date().toISOString().slice(0, 10)
            }
        }).then((res) => {
            setValues(new Map(Object.entries(res.data)));
        });

        axios.get("/schedules", {
            params: {
                dayOfWeek: dayOfWeek
            }
        }).then((res) => {
            setSchedules(res.data);
        }).catch((err) => console.error(err));

        axios.get("/mensa-menu")
        .then((res) => {
            const currentDate = new Date().toISOString().slice(0, 10).split("-").reverse().join(".");
            const dayMenu = (res.data as DayMenu[]).find((x: DayMenu) => x.date.includes(currentDate));
            setDayMenu(dayMenu);
        })
    }, []);

    const insertExpense = async () => {
        try {
            const args = expenseInput.split(" ");
            if (args.length < 2) {
                throw new Error("Argument must be of form: amount category [description]");
            }

            const amount = Math.round(parseFloat(args[0]));
            if (isNaN(amount)) {
                throw new Error("Argument amount must be a valid number");
            }

            const category = args[1];
            const description = args.length > 2 ? args[2] : undefined;

            const res = await axios.post("/expense", {
                amount, category, description
            });

            const data: Expense = res.data as Expense;
            if (values.has(data.category)) {
                const currentTotal = values.get(data.category);
                setValues((map) => new Map(map).set(
                    data.category, currentTotal + data.amount
                ));
            } else {
                setValues((map) => new Map(map).set(
                    data.category, data.amount
                ));
            }
            setExpenseInput("");
        } catch (error) {
            console.error(error);
        }
    }

    const keyDownHandler = async (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === "Enter") {
            await insertExpense();
        }
    }

    return (<div className="dashboard">
        <div>
            <PieChart sectors={values} radius={150}/>
            <input type="text" value={expenseInput}
                onChange={(e) => setExpenseInput(e.target.value)}
                onKeyDown={keyDownHandler}
                className="expense-input"></input>
        </div>
        <div>
            <h1> { new Date().toDateString() } </h1>
            <TimeTable schedules={schedules}/>
        </div>
        <div style={{maxWidth: "30em"}}>
            <h1> Mensa | Academica </h1>
            {dayMenu && <Menu items={dayMenu}/>}
        </div>
    </div>
    )
}

export default Dashboard;