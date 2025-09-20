import { PieChart } from "../layout/PieChart"

const Dashboard = () => {
    const values = new Map([
        ["test", 30]
    ])
    return (
        <PieChart sectors={values}/>
    )
}

export default Dashboard;