import { Metadata } from 'next'
import RegisterPage from "@/components/features/RegisterPage";

export const metadata: Metadata = {
    title: '注册界面',
    description: '注册界面'
}

export default function Home() {
    return <RegisterPage/>
}