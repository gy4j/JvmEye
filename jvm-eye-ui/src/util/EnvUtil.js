let envUtil = {
  logoutUrl: function() {
    if (window.location.href.startsWith('http://localhost')) {
      return 'http://localhost:9099/logout';
    } else {
      return '/logout';
    }
  },
};

export default envUtil;