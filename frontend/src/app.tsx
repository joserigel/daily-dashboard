import { createRoot } from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router';
import Index from './pages/Index';

const router = createBrowserRouter([
    {
        path: "/",
        Component: Index,
    }
])

const root = createRoot(document.body);
root.render(<RouterProvider router={router}/>);