import { useNavigate } from "react-router";
import "./Navbar.css";


interface NavButtonProps {
    text: string;
    to: string;
}

const NavButton = ({text, to}: NavButtonProps) => {
    const navigate = useNavigate();
    return (
        <button className="nav-button" onClick={() => navigate(to)}>
            { text }
        </button>
    )
}

export const Navbar = () => {
    return (<nav>
        <NavButton text="Home" to="/"/>
        <NavButton text="Preferences" to="/preferences"/>
    </nav>)
}
