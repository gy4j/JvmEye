module.exports = {
    lintOnSave: false,
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:9099',
                changeOrigin: true,
            },
        },
    },
};
