'use client'

import { useState } from 'react'
import { X } from 'lucide-react'
import { PORT } from "@/components/constraints"

interface AddEventModalProps {
  isOpen: boolean
  onClose: () => void
  onSuccess: () => void
}

export default function AddEventModal({ isOpen, onClose, onSuccess }: AddEventModalProps) {
  const [code, setCode] = useState('')
  const [file, setFile] = useState<File | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!code || !file) {
      setError('请填写灾情码并上传文件')
      return
    }

    if (!code.match(/^\d{36}$/)) {
      setError('灾情码必须是36位数字')
      return
    }

    try {
      setLoading(true)
      setError('')

      const formData = new FormData()
      formData.append('code', code)
      formData.append('file', file)

      const response = await fetch(`http://localhost:${PORT}/event/addEventWithMedia`, {
        method: 'POST',
        body: formData,
        credentials: 'include',
      })

      const result = await response.json()
      if (result.code === 0) {
        onSuccess()
        onClose()
        setCode('')
        setFile(null)
      } else {
        throw new Error(result.msg || '添加失败')
      }
    } catch (error) {
      console.error('添加失败:', error)
      setError(error instanceof Error ? error.message : '添加失败')
    } finally {
      setLoading(false)
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold">添加灾情记录</h2>
          <button
            onClick={onClose}
            className="p-1 hover:bg-gray-100 rounded-full"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          {error && (
            <div className="mb-4 p-2 bg-red-100 text-red-600 rounded">
              {error}
            </div>
          )}

          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              灾情码
            </label>
            <input
              type="text"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入36位灾情码"
              pattern="^\d{36}$"
              required
            />
          </div>

          <div className="mb-6">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              图片/视频
            </label>
            <input
              type="file"
              onChange={(e) => setFile(e.target.files?.[0] || null)}
              className="w-full"
              accept="image/*,video/*"
              required
            />
          </div>

          <div className="flex justify-end space-x-3">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-md"
              disabled={loading}
            >
              取消
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-blue-400"
              disabled={loading}
            >
              {loading ? '提交中...' : '提交'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
} 