import "./Navbar.css";


interface NavButtonProps {
    text: string;
    to: string;
}

const NavButton = ({text, to}: NavButtonProps) => {
    return (
        <button className="nav-button">
            { text }
        </button>
    )
}

export const Navbar = () => {
    return (<nav>
        <NavButton text="Home" to=""/>
        <NavButton text="Preferences" to=""/>
    </nav>)
}
