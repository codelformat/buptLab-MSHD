import Head from 'next/head';

function MyApp({ Component, pageProps }) {
    return (
        <>
            <Head>
                <script src="/_AMapSecurityConfig.js" />
            </Head>
            <Component {...pageProps} />
        </>
    );
}

export default MyApp; 