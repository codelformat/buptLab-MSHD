'use client'

import { useState, useEffect } from 'react'
import { Edit2, Trash2, MoreVertical, X, Archive, Save, RefreshCw, Clock } from 'lucide-react'

interface Event {
  code: string
  location: string
  time: string
  sourceCategory: string
  sourceSubcategory: string
  carrier: string
  disasterCategory: string
  disasterSubcategory: string
  disasterIndicator: string
  description: string
}

interface EditModalProps {
  event: Event | null
  isOpen: boolean
  onClose: () => void
  onSave: (event: Event) => void
}

const EditModal = ({ event, isOpen, onClose, onSave }: EditModalProps) => {
  const [formData, setFormData] = useState<Event | null>(null)

  useEffect(() => {
    setFormData(event)
  }, [event])

  if (!isOpen || !formData) return null

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setFormData(prev => prev ? { ...prev, [name]: value } : null)
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (formData) {
      onSave(formData)
    }
  }

  return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div className="bg-white rounded-lg p-6 w-full max-w-2xl">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold">编辑记录</h2>
            <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
              <X className="h-6 w-6" />
            </button>
          </div>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">编码</label>
                <input
                    type="text"
                    name="code"
                    value={formData.code}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">地点</label>
                <input
                    type="text"
                    name="location"
                    value={formData.location}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">时间</label>
                <input
                    type="text"
                    name="time"
                    value={formData.time}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">来源大类</label>
                <input
                    type="text"
                    name="sourceCategory"
                    value={formData.sourceCategory}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">来源子类</label>
                <input
                    type="text"
                    name="sourceSubcategory"
                    value={formData.sourceSubcategory}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">载体</label>
                <input
                    type="text"
                    name="carrier"
                    value={formData.carrier}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">灾情大类</label>
                <input
                    type="text"
                    name="disasterCategory"
                    value={formData.disasterCategory}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">灾情子类</label>
                <input
                    type="text"
                    name="disasterSubcategory"
                    value={formData.disasterSubcategory}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">灾情指标</label>
                <input
                    type="text"
                    name="disasterIndicator"
                    value={formData.disasterIndicator}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">描述</label>
              <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  rows={3}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              />
            </div>
            <div className="flex justify-end space-x-3 pt-4">
              <button
                  type="button"
                  onClick={onClose}
                  className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
              >
                取消
              </button>
              <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
              >
                保存
              </button>
            </div>
          </form>
        </div>
      </div>
  )
}

interface TimeWindowModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (days: number) => void;
  currentValue: number;
}

const TimeWindowModal = ({ isOpen, onClose, onSave, currentValue }: TimeWindowModalProps) => {
  const [days, setDays] = useState(currentValue);

  if (!isOpen) return null;

  return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div className="bg-white rounded-lg p-6 w-full max-w-md">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold">设置备份时间窗口</h2>
            <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
              <X className="h-6 w-6" />
            </button>
          </div>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">时间窗口（天）</label>
              <input
                  type="number"
                  min="1"
                  value={days}
                  onChange={(e) => setDays(parseInt(e.target.value) || currentValue)}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              />
            </div>
            <div className="flex justify-end space-x-3 pt-4">
              <button
                  onClick={onClose}
                  className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
              >
                取消
              </button>
              <button
                  onClick={() => {
                    if (days > 0) {
                      onSave(days);
                    }
                  }}
                  className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
              >
                保存
              </button>
            </div>
          </div>
        </div>
      </div>
  );
};

export default function DataTable({ searchQuery }: { searchQuery: string }) {
  const [data, setData] = useState<Event[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [editingEvent, setEditingEvent] = useState<Event | null>(null)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [isSearchingBackup, setIsSearchingBackup] = useState(false)
  const [isBackingUp, setIsBackingUp] = useState(false)
  const [isResetting, setIsResetting] = useState(false)
  const [timeWindowDays, setTimeWindowDays] = useState(365)
  const [isTimeWindowModalOpen, setIsTimeWindowModalOpen] = useState(false)

  useEffect(() => {
    // 获取当前的时间窗口设置
    fetch('http://localhost:12500/event/backup/time-window', {
      credentials: 'include',
    })
        .then(response => response.json())
        .then(result => {
          if (result.code === 0) {
            setTimeWindowDays(result.data);
          }
        })
        .catch(console.error);
  }, []);

  const handleSetTimeWindow = async (days: number) => {
    try {
      const response = await fetch('http://localhost:12500/event/backup/time-window', {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({ days }),
      });

      const result = await response.json();
      if (result.code === 0) {
        setTimeWindowDays(days);
        setIsTimeWindowModalOpen(false);
        alert('时间窗口设置成功');
      } else {
        throw new Error(result.msg || '设置失败');
      }
    } catch (error) {
      console.error('设置错误:', error);
      alert(error instanceof Error ? error.message : '设置失败');
    }
  };

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);
      const endpoint = isSearchingBackup ? 'backup/list' : 'list';
      const response = await fetch(`http://localhost:12500/event/${endpoint}?search=${encodeURIComponent(searchQuery)}`, {
        method: 'GET',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        credentials: 'include',
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      if (result.code === 0) {
        setData(result.data || []);
      } else {
        throw new Error(result.msg || 'Failed to fetch data');
      }
    } catch (error) {
      console.error('Error fetching data:', error);
      setError(error instanceof Error ? error.message : 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [searchQuery, isSearchingBackup]);

  const handleEdit = (event: Event) => {
    setEditingEvent(event);
    setIsModalOpen(true);
  };

  const handleDelete = async (code: string) => {
    if (!confirm('确定要删除这条记录吗？')) {
      return;
    }
    try {
      const response = await fetch(`http://localhost:12500/event/code/${code}`, {
        method: 'DELETE',
        credentials: 'include',
      });

      if (!response.ok) {
        throw new Error('删除失败');
      }

      const result = await response.json();
      if (result.code === 0) {
        alert('删除成功');
        fetchData(); // 刷新数据
      } else {
        throw new Error(result.msg || '删除失败');
      }
    } catch (error) {
      console.error('删除错误:', error);
      alert(error instanceof Error ? error.message : '删除失败');
    }
  };

  const handleSave = async (updatedEvent: Event) => {
    try {
      const response = await fetch(`http://localhost:12500/event/code/${updatedEvent.code}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(updatedEvent),
      });

      if (!response.ok) {
        throw new Error('更新失败');
      }

      const result = await response.json();
      if (result.code === 0) {
        alert('更新成功');
        setIsModalOpen(false);
        fetchData(); // 刷新数据
      } else {
        throw new Error(result.msg || '更新失败');
      }
    } catch (error) {
      console.error('更新错误:', error);
      alert(error instanceof Error ? error.message : '更新失败');
    }
  };

  const triggerBackup = async () => {
    if (!confirm('确定要立即备份数据吗？这将会把超过时间窗口的数据移动到备份数据库。')) {
      return;
    }

    try {
      setIsBackingUp(true);
      const response = await fetch('http://localhost:12500/event/backup/trigger', {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        credentials: 'include',
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      if (result.code === 0) {
        alert('数据备份成功');
        fetchData(); // 刷新数据
      } else {
        throw new Error(result.msg || '备份失败');
      }
    } catch (error) {
      console.error('备份错误:', error);
      alert(error instanceof Error ? error.message : '备份失败');
    } finally {
      setIsBackingUp(false);
    }
  };

  const resetBackup = async () => {
    if (!confirm('确定要重置备份表吗？这将删除所有备份数据并重新创建备份表。')) {
      return;
    }

    try {
      setIsResetting(true);
      const response = await fetch('http://localhost:12500/event/backup/reset', {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        credentials: 'include',
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      if (result.code === 0) {
        alert('备份表重置成功');
        if (isSearchingBackup) {
          fetchData(); // 如果当前在查看备份数据，则刷新数据
        }
      } else {
        throw new Error(result.msg || '重置失败');
      }
    } catch (error) {
      console.error('重置错误:', error);
      alert(error instanceof Error ? error.message : '重置失败');
    } finally {
      setIsResetting(false);
    }
  };

  if (error) {
    return (
        <div className="bg-red-50 p-4 rounded-lg text-red-600">
          Error: {error}
        </div>
    );
  }

  return (
      <>
        <div className="mb-4 flex justify-end space-x-2">
          <button
              onClick={() => setIsTimeWindowModalOpen(true)}
              className="flex items-center px-4 py-2 rounded-md bg-purple-600 text-white hover:bg-purple-700"
          >
            <Clock className="h-4 w-4 mr-2" />
            设置时间窗口 ({timeWindowDays}天)
          </button>
          <button
              onClick={resetBackup}
              disabled={isResetting}
              className={`flex items-center px-4 py-2 rounded-md bg-red-600 text-white hover:bg-red-700 disabled:bg-red-400 disabled:cursor-not-allowed`}
          >
            <RefreshCw className="h-4 w-4 mr-2" />
            {isResetting ? '重置中...' : '重置备份表'}
          </button>
          <button
              onClick={triggerBackup}
              disabled={isBackingUp}
              className={`flex items-center px-4 py-2 rounded-md bg-green-600 text-white hover:bg-green-700 disabled:bg-green-400 disabled:cursor-not-allowed`}
          >
            <Save className="h-4 w-4 mr-2" />
            {isBackingUp ? '备份中...' : '立即备份'}
          </button>
          <button
              onClick={() => setIsSearchingBackup(!isSearchingBackup)}
              className={`flex items-center px-4 py-2 rounded-md ${
                  isSearchingBackup
                      ? 'bg-yellow-600 text-white hover:bg-yellow-700'
                      : 'bg-blue-600 text-white hover:bg-blue-700'
              }`}
          >
            <Archive className="h-4 w-4 mr-2" />
            {isSearchingBackup ? '查看当前数据' : '查看备份数据'}
          </button>
        </div>

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  编码
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  地点
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  时间
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  来源大类
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  来源子类
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  载体
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  灾情大类
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  灾情子类
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  灾情指标
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  描述
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  操作
                </th>
              </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
              {loading ? (
                  <tr>
                    <td colSpan={11} className="px-4 py-4 text-center">
                      加载中...
                    </td>
                  </tr>
              ) : data.length === 0 ? (
                  <tr>
                    <td colSpan={11} className="px-4 py-4 text-center">
                      暂无数据
                    </td>
                  </tr>
              ) : (
                  data.map((item) => (
                      <tr key={item.code} className="hover:bg-gray-50">
                        <td className="px-4 py-4 text-sm whitespace-nowrap">{item.code}</td>
                        <td className="px-4 py-4 text-sm">{item.location}</td>
                        <td className="px-4 py-4 text-sm whitespace-nowrap">{item.time}</td>
                        <td className="px-4 py-4 text-sm">{item.sourceCategory}</td>
                        <td className="px-4 py-4 text-sm">{item.sourceSubcategory}</td>
                        <td className="px-4 py-4 text-sm">{item.carrier}</td>
                        <td className="px-4 py-4 text-sm">{item.disasterCategory}</td>
                        <td className="px-4 py-4 text-sm">{item.disasterSubcategory}</td>
                        <td className="px-4 py-4 text-sm">{item.disasterIndicator}</td>
                        <td className="px-4 py-4 text-sm max-w-xs truncate">{item.description}</td>
                        <td className="px-4 py-4 whitespace-nowrap">
                          <div className="flex space-x-2">
                            <button
                                onClick={() => handleEdit(item)}
                                className="text-blue-600 hover:text-blue-800"
                                title="编辑"
                            >
                              <Edit2 className="h-4 w-4" />
                            </button>
                            <button
                                onClick={() => handleDelete(item.code)}
                                className="text-red-600 hover:text-red-800"
                                title="删除"
                            >
                              <Trash2 className="h-4 w-4" />
                            </button>
                            <button
                                className="text-gray-400 hover:text-gray-600"
                                title="更多"
                            >
                              <MoreVertical className="h-4 w-4" />
                            </button>
                          </div>
                        </td>
                      </tr>
                  ))
              )}
              </tbody>
            </table>
          </div>
        </div>
        <EditModal
            event={editingEvent}
            isOpen={isModalOpen}
            onClose={() => {
              setIsModalOpen(false);
              setEditingEvent(null);
            }}
            onSave={handleSave}
        />

        <TimeWindowModal
            isOpen={isTimeWindowModalOpen}
            onClose={() => setIsTimeWindowModalOpen(false)}
            onSave={handleSetTimeWindow}
            currentValue={timeWindowDays}
        />
      </>
  )
}