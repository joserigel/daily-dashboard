export interface MenuItem {
    price: string;
    name: string;
    category: string;
}

export interface DayMenu {
    date: string;
    menu: MenuItem[];
}