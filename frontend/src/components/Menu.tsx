import { stringToColor } from "../utils/stringToColor";
import "./Menu.css";
import { DayMenu } from "../models/MenuItem"

interface MenuProps {
    items: DayMenu;
}

export const Menu = ({items, ...rest}: MenuProps) => {

    const colorMap = new Map([
        ["Vegetarian", "green"],
        ["Classics", "red"],
        ["Express", "red"],
        ["Stew", "brown"]
    ]);

    return (
        <div className="menu" {...rest}>
            {
                items.menu.map((item, i) => <div 
                    key={i} 
                    style={{
                        borderColor: colorMap.get(item.category) ?? "orange"
                    }}>
                    <h1>{ item.name }</h1>
                    <h3>{ item.price }</h3>
                    <h3>{ item.category }</h3>
                </div>)
            }
        </div>
    )
}