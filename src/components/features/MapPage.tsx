// components/features/MapPage.tsx
'use client';

import React, { useEffect, useState } from 'react';
import Script from 'next/script';

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

  class PlaceSearch {
    constructor(options?: {
      city?: string;
      pageSize?: number;
      pageIndex?: number;
    });
    search(
      keyword: string,
      callback: (status: string, result: any) => void
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
        const res = await fetch('http://localhost:8080/stats/getStats');
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
    if (!mapLoaded) {
      console.log('等待地图 API 加载...');
      return;
    }
    if (data.length === 0) {
      console.log('等待数据加载...');
      return;
    }

    console.log('开始初始化地图，当前状态:', {
      mapLoaded,
      dataLength: data.length,
      windowAMap: typeof window !== 'undefined' ? !!(window as any).AMap : false
    });

    // 确保 AMap 已定义
    if (typeof AMap === 'undefined') {
      console.error('AMap 未定义');
      return;
    }

    const initMap = () => {
      try {
        console.log('=== 开始初始化地图 ===');

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

  return (
    <div>
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

      {loading && <div>数据加载中...</div>}
      {error && <div className="text-red-500">{error}</div>}
      {!mapLoaded && <div>地图加载中...</div>}
      <div id="mapContainer" style={{ width: '100%', height: '500px' }}></div>
    </div>
  );
};

export default MapPage;
