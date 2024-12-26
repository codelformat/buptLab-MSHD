import { Metadata } from 'next'
import LoginPage from '@/components/features/LoginPage'

export const metadata: Metadata = {
    title: '登录界面',
    description: '登陆界面'
}

export default function Home() {
    return <LoginPage/>
}