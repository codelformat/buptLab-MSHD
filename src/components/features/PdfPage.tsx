'use client';

import React from 'react';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import { PORT } from "@/components/constraints";
import { PieChart, Pie, BarChart, Bar, XAxis, YAxis, Tooltip, Cell, LineChart, Line } from 'recharts';

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

const PdfPage: React.FC = () => {
    const [data, setData] = React.useState<RecordType[]>([]);
    const [loading, setLoading] = React.useState(true);

    React.useEffect(() => {
        const fetchData = async () => {
            try {
                const res = await fetch(`http://localhost:${PORT}/stats/getStats`);
                const json = await res.json();
                if (json.data) {
                    setData(json.data);
                }
            } catch (err) {
                console.error('获取数据失败:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    // 统计函数
    const getDisasterStats = () => {
        const stats = data.reduce((acc, item) => {
            acc[item.disasterCategory] = (acc[item.disasterCategory] || 0) + 1;
            return acc;
        }, {} as Record<string, number>);
        return Object.entries(stats).map(([name, value]) => ({ name, value }));
    };

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

    const generatePDF = async () => {
        const element = document.getElementById('pdf-content');
        if (!element) return;

        try {
            const canvas = await html2canvas(element);
            const imgData = canvas.toDataURL('image/png');

            const pdf = new jsPDF('p', 'mm', 'a4');
            const pdfWidth = pdf.internal.pageSize.getWidth();
            const pdfHeight = pdf.internal.pageSize.getHeight();

            pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);
            pdf.save('灾情统计报告.pdf');
        } catch (error) {
            console.error('PDF生成失败:', error);
        }
    };

    if (loading) {
        return <div>加载中...</div>;
    }

    return (
        <div className="p-8">
            <button
                onClick={generatePDF}
                className="mb-8 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
                下载PDF报告
            </button>

            <div id="pdf-content" className="space-y-8">
                <h1 className="text-2xl font-bold text-center">灾情统计报告</h1>

                {/* 基本统计信息 */}
                <div className="bg-white p-6 rounded-lg shadow">
                    <h2 className="text-xl font-semibold mb-4">基本统计</h2>
                    <div className="grid grid-cols-2 gap-4">
                        <div className="bg-gray-50 p-4 rounded">
                            <p className="text-gray-600">总灾情数</p>
                            <p className="text-2xl font-bold">{data.length}</p>
                        </div>
                        <div className="bg-gray-50 p-4 rounded">
                            <p className="text-gray-600">最近24小时</p>
                            <p className="text-2xl font-bold">
                                {data.filter(item => {
                                    const itemDate = new Date(item.time);
                                    const now = new Date();
                                    return now.getTime() - itemDate.getTime() <= 24 * 60 * 60 * 1000;
                                }).length}
                            </p>
                        </div>
                    </div>
                </div>

                {/* 图表区域 */}
                <div className="grid grid-cols-2 gap-8">
                    {/* 饼图 */}
                    <div className="bg-white p-6 rounded-lg shadow">
                        <h3 className="text-lg font-semibold mb-4">灾害类型分布</h3>
                        <PieChart width={400} height={300}>
                            <Pie
                                data={getDisasterStats()}
                                cx={200}
                                cy={150}
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

                    {/* 时间趋势图 */}
                    <div className="bg-white p-6 rounded-lg shadow">
                        <h3 className="text-lg font-semibold mb-4">时间分布趋势</h3>
                        <LineChart width={400} height={300} data={getTimeStats()}>
                            <XAxis dataKey="date" />
                            <YAxis />
                            <Tooltip />
                            <Line type="monotone" dataKey="count" stroke="#8884d8" />
                        </LineChart>
                    </div>
                </div>

                {/* 详细列表 */}
                <div className="bg-white p-6 rounded-lg shadow">
                    <h2 className="text-xl font-semibold mb-4">灾情详细列表</h2>
                    <div className="space-y-4">
                        {data.map((item) => (
                            <div key={item.code} className="border-b pb-4">
                                <div className="font-medium text-blue-600">{item.disasterCategory}</div>
                                <div className="text-sm text-gray-600">{item.location}</div>
                                <div className="text-sm text-gray-500">
                                    {new Date(item.time).toLocaleString()}
                                </div>
                                <div className="text-sm text-gray-700 mt-1">{item.description}</div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PdfPage;
