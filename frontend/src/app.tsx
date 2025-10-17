import { createRoot } from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router';
import Index from './pages/Index';
import Preferences from "./pages/Preferences";
import Dashboard from "./pages/Dashboard";
import axios from 'axios';

const router = createBrowserRouter([
    {
        path: "/",
        Component: Index,
        children: [
            {
                path: "/preferences" ,
                Component: Preferences
            },
            {
                path: "/",
                Component: Dashboard
            }
        ]
    },
])

axios.defaults.baseURL = "http://localhost:8080";

const root = createRoot(document.body);
root.render(<RouterProvider router={router}/>);