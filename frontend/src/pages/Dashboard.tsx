import { PieChart } from "../layout/PieChart"

const Dashboard = () => {
    const values = new Map([
        ["groceries", 30],
        ["food", 500],
        ["bob", 30],
        ["bloob", 30],
        ["bleeb", 50]
    ])
    return (
        <PieChart sectors={values} radius={150}/>
    )
}

export default Dashboard;