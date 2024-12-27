'use client'

import { Home, Database, History, FileText, BarChart2, Settings, FileDown } from 'lucide-react'
import Link from 'next/link'
import { usePathname } from 'next/navigation'

export default function Sidebar() {
  const pathname = usePathname()

  const menuItems = [
    { icon: Home, label: '首页', path: '/' },
    { icon: Database, label: '灾情数据记录', path: '/records' },
    { icon: History, label: '生成报表分析', path: '/analysis' },
    { icon: FileText, label: '原始数据', path: '/raw' },
    { icon: BarChart2, label: '统计分析', path: '/stats' },
    { icon: Settings, label: '系统设置', path: '/settings' },
    { icon: FileDown, label: '下载报告', path: '/downloadPdf' }
  ]

  return (
    <aside className="w-64 bg-white border-r border-gray-200">
      <div className="p-6">
        <h2 className="text-xl font-bold">MSHD灾情管理系统</h2>
      </div>
      <nav className="mt-6">
        {menuItems.map((item) => {
          const Icon = item.icon
          return (
            <Link
              key={item.path}
              href={item.path}
              className={`flex items-center px-6 py-3 text-gray-700 hover:bg-gray-100 ${pathname === item.path ? 'bg-blue-50 text-blue-600' : ''
                }`}
            >
              <Icon className="h-5 w-5 mr-3" />
              <span>{item.label}</span>
            </Link>
          )
        })}
      </nav>
    </aside>
  )
} 