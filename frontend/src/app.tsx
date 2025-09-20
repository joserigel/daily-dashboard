import { createRoot } from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router';
import Index from './pages/Index';
import Preferences from "./pages/Preferences";
import Dashboard from "./pages/Dashboard";

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

const root = createRoot(document.body);
root.render(<RouterProvider router={router}/>);