'use client'

import { useState, useEffect } from 'react'
import { Edit2, Trash2, MoreVertical } from 'lucide-react'

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

export default function DataTable({ searchQuery }: { searchQuery: string }) {
  const [data, setData] = useState<Event[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await fetch('http://localhost:12500/event/list', {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
          },
          credentials: 'include', // 必须加上这行
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

    fetchData();
  }, [searchQuery]);

  const handleEdit = async (id: number) => {
    console.log('Edit item:', id);
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this item?')) {
      return;
    }
    try {
      const response = await fetch(`http://localhost:12500/event/${id}`, {
        method: 'DELETE',
      });
      if (!response.ok) {
        throw new Error('Failed to delete item');
      }
      const fetchData = async () => {
        const response = await fetch('http://localhost:12500/event/list');
        if (!response.ok) {
          throw new Error('Failed to fetch data after deletion');
        }
        const result = await response.json();
        if (result.code === 0) {
          setData(result.data || []);
        }
      };
      await fetchData();
    } catch (error) {
      console.error('Error deleting item:', error);
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
              data.map((item, index) => (
                <tr key={index} className="hover:bg-gray-50">
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
                        onClick={() => handleEdit(index)}
                        className="text-blue-600 hover:text-blue-800"
                        title="编辑"
                      >
                        <Edit2 className="h-4 w-4" />
                      </button>
                      <button 
                        onClick={() => handleDelete(index)}
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
  )
} 