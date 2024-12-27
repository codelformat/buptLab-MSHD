// components/features/MapPage.tsx
'use client';

import React, { useEffect, useState } from 'react';
import Script from 'next/script';
import { PieChart, Pie, BarChart, Bar, XAxis, YAxis, Tooltip, Cell, LineChart, Line } from 'recharts';

declare module 'recharts' {
  export interface Props {
    children?: React.ReactNode;
    width?: number;
    height?: number;
    data?: any[];
    dataKey?: string;
    name?: string;
    cx?: number;
    cy?: number;
    innerRadius?: number;
    outerRadius?: number;
    paddingAngle?: number;
    fill?: string;
  }

  export class PieChart extends React.Component<Props> { }
  export class Pie extends React.Component<Props> { }
  export class BarChart extends React.Component<Props> { }
  export class Bar extends React.Component<Props> { }
  export class XAxis extends React.Component<Props> { }
  export class YAxis extends React.Component<Props> { }
  export class Tooltip extends React.Component<Props> { }
  export class Cell extends React.Component<Props> { }
  export class LineChart extends React.Component<Props> { }
  export class Line extends React.Component<Props> { }
}
import { PORT } from "@/components/constraints";

declare namespace AMap {
  class Map {
    constructor(container: string, options: any);
    destroy(): void;
    setFitView(): void;
  }

  class Marker {
    constructor(options: {
      position: [number, number];
      map: Map;
      title?: string;
    });
    on(event: string, handler: Function): void;
    getPosition(): any;
  }

  class InfoWindow {
    constructor(options: {
      content: string;
      offset: Pixel;
    });
    open(map: Map, position: any): void;
  }

  class Pixel {
    constructor(x: number, y: number);
  }

  class Geocoder {
    constructor(options?: { city?: string });
    getLocation(
      address: string,
      callback: (status: string, result: any) => void
    ): void;
  }

  interface PlaceSearchResult {
    poiList: {
      pois: Array<{
        location: {
          lng: number;
          lat: number;
        };
        name: string;
        address: string;
      }>;
    };
  }

  class PlaceSearch {
    constructor(options?: {
      city?: string;
      pageSize?: number;
      pageIndex?: number;
    });
    search(
      keyword: string,
      callback: (status: 'complete' | 'error' | 'no_data', result: {
        poiList?: {
          pois?: Array<{
            location: {
              lng: number;
              lat: number;
            };
          }>;
        };
      }) => void
    ): void;
  }

  interface GeocoderResult {
    geocodes: Array<{
      location: {
        lng: number;
        lat: number;
      };
    }>;
  }
}

declare global {
  interface Window {
    _AMapSecurityConfig: {
      securityJsCode: string | undefined;
    };
  }
}

// 定义数据类型，根据后端返回的数据结构进行调整
interface RecordType {
  code: string;
  location: string;
  time: string;
  sourceCategory: string;
  sourceSubcategory: string | null;
  carrier: string;
  disasterCategory: string;
  disasterSubcategory: string;
  disasterIndicator: string;
  description: string;
}

const MapPage: React.FC = () => {
  const [data, setData] = useState<RecordType[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [mapLoaded, setMapLoaded] = useState<boolean>(false);

  // 在组件顶部添加安全码设置
  useEffect(() => {
    // 设置安全码配置
    if (typeof window !== 'undefined') {
      window._AMapSecurityConfig = {
        securityJsCode: process.env.NEXT_PUBLIC_AMAP_SECURITY_CODE
      };
    }
  }, []);  // 空依赖数组，只在组件挂载时执行一次

  // 添加一个 window 加载检查
  useEffect(() => {
    if (typeof window !== 'undefined' && (window as any).AMap) {
      console.log('AMap 已存在于 window 对象中');
      setMapLoaded(true);
    }
  }, []);

  // 单独的数据获取 useEffect
  useEffect(() => {
    const fetchData = async () => {
      try {
        console.log('开始获取数据...');
        const res = await fetch(`http://localhost:${PORT}/stats/getStats`);
        if (!res.ok) {
          throw new Error('Failed to fetch data');
        }
        const json = await res.json();
        console.log('获取到的原始数据:', json);

        if (json.data && Array.isArray(json.data)) {
          setData(json.data);
          console.log('数据已设置:', json.data);
        } else {
          console.error('数据格式不正确');
          setError('数据格式不正确');
        }
      } catch (err) {
        console.error('获取数据失败:', err);
        setError('获取数据失败');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []); // 只在组件挂载时执行一次

  // 地图初始化
  useEffect(() => {
    if (!mapLoaded || !data.length || typeof window === 'undefined' || !(window as any).AMap) {
      console.log('等待地图 API 和数据加载...');
      return;
    }

    console.log('开始初始化地图���当前状态:', {
      mapLoaded,
      dataLength: data.length,
      windowAMap: !!(window as any).AMap
    });

    const initMap = () => {
      try {
        console.log('=== 开始初始化地图 ===');
        const AMap = (window as any).AMap;

        const map = new AMap.Map('mapContainer', {
          center: [116.397428, 39.90923],
          zoom: 5,
        });

        // 使用 PlaceSearch 替代 Geocoder
        const placeSearch = new AMap.PlaceSearch({
          pageSize: 1,
          pageIndex: 1,
          extensions: 'all',
          citylimit: false
        });

        // 处理每个位置
        data.forEach((item, index) => {
          const address = item.location;
          console.log(`处理第 ${index + 1}/${data.length} 条数据:`, address);

          placeSearch.search(address, (status, result) => {
            console.log('搜索回调:', {
              地址: address,
              状态: status,
              结果: result
            });

            if (status === 'complete' && result.poiList?.pois?.length > 0) {
              const location = result.poiList.pois[0].location;
              console.log('=== 地址解析成功 ===');
              console.log(`${address} -> 经度:${location.lng}, 纬度:${location.lat}`);

              // 创建标记
              const marker = new AMap.Marker({
                position: [location.lng, location.lat],
                map: map,
                title: `${item.disasterCategory} (${location.lng}, ${location.lat})`
              });

              // 创建信息窗口
              const infoWindow = new AMap.InfoWindow({
                content: `
                  <div style="padding: 10px;">
                    <h3 style="margin:0;color:#0088ff;">${item.location}</h3>
                    <p style="margin:5px 0;">时间: ${new Date(item.time).toLocaleString()}</p>
                    <p style="margin:5px 0;">描述: ${item.description}</p>
                  </div>
                `,
                offset: new AMap.Pixel(0, -30)
              });

              marker.on('click', () => {
                infoWindow.open(map, marker.getPosition());
              });
            } else {
              console.error('地址搜索失败:', {
                状态: status,
                地址: address,
                结果: result
              });
            }
          });
        });

      } catch (err) {
        console.error('地图初始化失败:', err);
        setError('地图初始化失败');
      }
    };

    // 延迟初始化地图，确保 DOM 和 API 都已准备就绪
    setTimeout(initMap, 1000);

    return () => {
      // 理函数
      const mapInstance = (window as any).__amap_init_map;
      if (mapInstance) {
        mapInstance.destroy();
      }
    };
  }, [mapLoaded, data]);

  // 添加图表数据处理函数
  const getDisasterStats = () => {
    const stats = data.reduce((acc, item) => {
      acc[item.disasterCategory] = (acc[item.disasterCategory] || 0) + 1;
      return acc;
    }, {} as Record<string, number>);

    return Object.entries(stats).map(([name, value]) => ({ name, value }));
  };

  // 添加时间分布统计函数
  const getTimeStats = () => {
    const timeStats = data.reduce((acc, item) => {
      const date = new Date(item.time).toLocaleDateString();
      acc[date] = (acc[date] || 0) + 1;
      return acc;
    }, {} as Record<string, number>);

    return Object.entries(timeStats)
      .map(([date, count]) => ({ date, count }))
      .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
  };

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

  return (
    <div className="flex flex-col h-screen">
      {/* 顶栏 - 更新样式 */}
      <div className="bg-gradient-to-r from-indigo-600 to-blue-500 text-white p-4 shadow-md flex justify-between items-center">
        <h1 className="text-xl font-bold">灾情信息地图</h1>
        <div className="flex gap-4">
          <div className="bg-white/10 rounded-lg p-3 backdrop-blur-sm">
            <div className="text-sm opacity-80">总灾情数</div>
            <div className="text-2xl font-bold">{data.length}</div>
          </div>
          <div className="bg-white/10 rounded-lg p-3 backdrop-blur-sm">
            <div className="text-sm opacity-80">最近24小时</div>
            <div className="text-2xl font-bold">
              {data.filter(item => {
                const itemDate = new Date(item.time);
                const now = new Date();
                const diff = now.getTime() - itemDate.getTime();
                return diff <= 24 * 60 * 60 * 1000;
              }).length}
            </div>
          </div>
        </div>
      </div>

      <div className="flex flex-1 overflow-hidden">
        {/* 左侧边栏 */}
        <div className="w-80 bg-white shadow-lg overflow-y-auto p-4">
          <h2 className="text-lg font-semibold mb-4">灾情列表</h2>
          {loading && <div className="text-gray-500">数据加载中...</div>}
          {error && <div className="text-red-500">{error}</div>}

          <div className="space-y-4">
            {data.map((item, index) => (
              <div
                key={item.code}
                className="border rounded-lg p-3 hover:bg-gray-50 cursor-pointer"
              >
                <div className="font-medium text-blue-600">{item.disasterCategory}</div>
                <div className="text-sm text-gray-600">{item.location}</div>
                <div className="text-sm text-gray-500">
                  {new Date(item.time).toLocaleString()}
                </div>
                <div className="text-sm text-gray-700 mt-1 line-clamp-2">
                  {item.description}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* 地图容器 */}
        <div className="flex-1 relative">
          {!mapLoaded && (
            <div className="absolute inset-0 flex items-center justify-center bg-gray-100">
              <div className="text-gray-500">地图加载中...</div>
            </div>
          )}
          <div id="mapContainer" className="w-full h-full" />
        </div>

        {/* 右侧图表栏 */}
        <div className="w-96 bg-white shadow-lg overflow-y-auto p-4">
          <h2 className="text-lg font-semibold mb-4">数据统计</h2>

          {/* 饼图 */}
          <div className="mb-8">
            <h3 className="text-sm font-medium text-gray-600 mb-2">灾害类型分布</h3>
            <PieChart width={300} height={200}>
              <Pie
                data={getDisasterStats()}
                cx={150}
                cy={100}
                innerRadius={60}
                outerRadius={80}
                paddingAngle={5}
                dataKey="value"
              >
                {getDisasterStats().map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </div>

          {/* 柱状图 */}
          <div>
            <h3 className="text-sm font-medium text-gray-600 mb-2">灾害频率统计</h3>
            <BarChart width={300} height={200} data={getDisasterStats()}>
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="value">
                {getDisasterStats().map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Bar>
            </BarChart>
          </div>

          {/* 时间分布折线图 */}
          <div className="mt-8">
            <h3 className="text-sm font-medium text-gray-600 mb-2">时间分布趋势</h3>
            <LineChart width={300} height={200} data={getTimeStats()}>
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip />
              <Line type="monotone" dataKey="count" stroke="#8884d8" />
            </LineChart>
          </div>
        </div>
      </div>

      <Script
        strategy="afterInteractive"
        src={`https://webapi.amap.com/maps?v=2.0&key=${process.env.NEXT_PUBLIC_AMAP_API_KEY}&plugin=AMap.PlaceSearch`}
        onLoad={() => {
          console.log('高德地图 API 加载完成，包含 PlaceSearch 插件');
          setMapLoaded(true);
        }}
        onError={(e) => {
          console.error('高德地图 API 加载失败:', e);
          setError('高德地图 API 加载失败');
        }}
      />
    </div>
  );
};

export default MapPage;
