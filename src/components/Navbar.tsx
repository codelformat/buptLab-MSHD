"use client";

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { PORT } from './constraints';

export default function Navbar() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [username, setUsername] = useState('');
    const [avatarUrl, setAvatarUrl] = useState('');
    const router = useRouter();

    useEffect(() => {
        checkAuth();
    }, []);

    const checkAuth = async () => {
        try {
            console.log('Checking auth...');
            const response = await fetch(`http://localhost:${PORT}/user/check-auth`, {
                method: 'GET',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                }
            });
            const data = await response.json();
            console.log('Auth response:', data);
            setIsAuthenticated(data.authenticated);
            if (data.authenticated) {
                setUsername(data.username);
                setAvatarUrl(data.avatarUrl);
                console.log('User authenticated:', {
                    username: data.username,
                    avatarUrl: data.avatarUrl
                });
            }
        } catch (error) {
            console.error('Error checking auth:', error);
        }
    };

    const handleLogout = async () => {
        try {
            const response = await fetch(`http://localhost:${PORT}/user/logout`, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                }
            });
            const data = await response.json();
            console.log('Logout response:', data);
            setIsAuthenticated(false);
            setUsername('');
            setAvatarUrl('');
            router.push('/login');
        } catch (error) {
            console.error('Error logging out:', error);
        }
    };

    console.log('Current state:', { isAuthenticated, username, avatarUrl });

    return (
        <nav className="bg-white shadow-lg">
            <div className="max-w-6xl mx-auto px-4">
                <div className="flex justify-between items-center h-16">
                    <div className="flex items-center">
                        <Link href="/" className="text-xl font-bold text-gray-800">
                            灾情管理系统
                        </Link>
                    </div>

                    <div className="flex items-center space-x-4">
                        {isAuthenticated ? (
                            <>
                                <div className="flex items-center space-x-3">
                                    <div className="w-8 h-8 rounded-full overflow-hidden bg-gray-200">
                                        {avatarUrl && (
                                            <img
                                                src={avatarUrl}
                                                alt={`${username}'s avatar`}
                                                className="w-full h-full object-cover"
                                            />
                                        )}
                                    </div>
                                    <span className="text-gray-600">欢迎, {username}</span>
                                </div>
                                <button
                                    onClick={handleLogout}
                                    className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-md"
                                >
                                    退出登录
                                </button>
                            </>
                        ) : (
                            <>
                                <Link
                                    href="/login"
                                    className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-md"
                                >
                                    登录
                                </Link>
                                <Link
                                    href="/register"
                                    className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-md"
                                >
                                    注册
                                </Link>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
} 