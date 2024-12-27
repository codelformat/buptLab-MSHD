'use client'

import { useState } from 'react'
import Sidebar from '@/components/layout/Sidebar'
import Header from '@/components/layout/Header'
import DataTable from '@/components/features/DataTable'
import AddEventModal from '@/components/features/AddEventModal'
import { Search, Upload, Plus } from 'lucide-react'

export default function Dashboard() {
  const [searchQuery, setSearchQuery] = useState('')
  const [uploading, setUploading] = useState(false)
  const [isAddModalOpen, setIsAddModalOpen] = useState(false)

  const handleFileUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return

    // 检查文件类型
    if (!file.name.endsWith('.xls') && !file.name.endsWith('.xlsx')) {
      alert('请上传 Excel 文件 (.xls 或 .xlsx)')
      return
    }

    try {
      setUploading(true)
      const formData = new FormData()
      formData.append('file', file)

      const response = await fetch('http://localhost:12500/event/addXlsTextItems', {
        method: 'POST',
        body: formData,
        credentials: 'include',
      })

      if (!response.ok) {
        throw new Error('上传失败')
      }

      const result = await response.json()
      if (result.code === 0) {
        alert('文件上传成功')
        // 触发数据表刷新
        setSearchQuery(prev => prev + ' ') // 通过改变 searchQuery 触发重新获取数据
      } else {
        throw new Error(result.msg || '上传失败')
      }
    } catch (error) {
      console.error('上传错误:', error)
      alert(error instanceof Error ? error.message : '文件上传失败')
    } finally {
      setUploading(false)
      // 清空文件输入框
      event.target.value = ''
    }
  }

  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-x-hidden overflow-y-auto bg-gray-50 p-6">
          <div className="mb-6 flex justify-between items-center">
            <h1 className="text-2xl font-semibold text-gray-900">灾情数据记录</h1>
            <div className="flex items-center gap-4">
              <div className="relative">
                <input
                  type="text"
                  placeholder="搜索记录..."
                  className="pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
                <Search className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
              </div>
              <button
                onClick={() => setIsAddModalOpen(true)}
                className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
              >
                <Plus className="h-5 w-5" />
                增加条目
              </button>
              <div className="relative">
                <input
                  type="file"
                  id="file-upload"
                  className="hidden"
                  accept=".xls,.xlsx"
                  onChange={handleFileUpload}
                  disabled={uploading}
                />
                <label
                  htmlFor="file-upload"
                  className={`flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg cursor-pointer hover:bg-blue-700 ${
                    uploading ? 'opacity-50 cursor-not-allowed' : ''
                  }`}
                >
                  <Upload className="h-5 w-5" />
                  {uploading ? '上传中...' : '上传 Excel'}
                </label>
              </div>
            </div>
          </div>
          <DataTable searchQuery={searchQuery} />
        </main>
      </div>
      <AddEventModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSuccess={() => {
          setSearchQuery(prev => prev + ' ') // 触发数据表刷新
        }}
      />
    </div>
  )
} 