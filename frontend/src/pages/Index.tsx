import { Outlet } from "react-router";
import { Navbar } from "../components/Navbar";
import "./app.css"

const Index = () => {
    return (<main>
        <Navbar/>
        <article>
            <Outlet/>
        </article>
    </main>)
}

export default Index;