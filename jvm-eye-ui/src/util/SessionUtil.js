export function setSession(user) {
  if (user) {
    sessionStorage.setItem('userId', user['id']);
    sessionStorage.setItem('username', user['username']);
    sessionStorage.setItem('sessionId', user['sessionId']);
  } else {
    sessionStorage.setItem('userId', '');
    sessionStorage.setItem('username', '');
    sessionStorage.setItem('sessionId', '');
  }
}

export function getUsername() {
  if (sessionStorage.getItem('username')) {
    return sessionStorage.getItem('username');
  }
  return null;
}

export function getUserId() {
  if (sessionStorage.getItem('userId')) {
    return sessionStorage.getItem('userId');
  }
  return null;
}


export function getSessionId() {
  if (sessionStorage.getItem('sessionId')) {
    return sessionStorage.getItem('sessionId');
  }
  return null;
}

