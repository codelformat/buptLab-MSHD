import { Metadata } from 'next'
import Dashboard from '@/components/Dashboard'

export const metadata: Metadata = {
  title: 'Data Management Dashboard',
  description: 'Manage and view data records'
}

export default function Home() {
  return <Dashboard />
} 