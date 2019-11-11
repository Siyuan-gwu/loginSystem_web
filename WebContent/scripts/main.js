(function() {
	/**
	 * Initialize major event handlers
	 */
	function init() {
		// register event listeners
		document.querySelector('#login-form-btn').addEventListener('click',
				validateSession);
		document.querySelector('#login-btn').addEventListener('click',
				checkLogin);
		document.querySelector('#register-form-btn').addEventListener('click',
				showRegisterForm);
		document.querySelector('#register-btn').addEventListener('click',
				register);
		document.querySelector('#logout-link').addEventListener('click',
				logout);
		validateSession();
		// onSessionValid({"user_id":"1111", "status":"OK"});
	}

	/**
	 * Session
	 */
	function validateSession() {
		onSessionInvalid();
		// The request parameters
		var url = './login';
		var req = JSON.stringify({});

		// make AJAX call
		ajax('GET', url, req,
		// session is still valid
		function(res) {
			var result = JSON.parse(res);

			if (result.status === 'OK') {
				onSessionValid(result);
			} else {
				showLoginError("登陆已过期，请重新登陆！");
			}
		});
	}

	function onSessionValid(result) {
		user_id = result.user_id;

		var loginForm = document.querySelector('#login-form');
		var registerForm = document.querySelector('#register-form');
		var welcomeMsg = document.querySelector('#welcome-msg');
		var logoutBtn = document.querySelector('#logout-link');
		welcomeMsg.innerHTML = '登陆成功！' + user_id;
		clearLoginError();
		showElement(logoutBtn, 'inline-block');
		showElement(loginForm);
		showElement(welcomeMsg);
		hideElement(registerForm);
	}

	function onSessionInvalid() {
		var loginForm = document.querySelector('#login-form');
		var registerForm = document.querySelector('#register-form');
		var welcomeMsg = document.querySelector('#welcome-msg');
		var logoutBtn = document.querySelector('#logout-link');

		hideElement(logoutBtn);
		hideElement(registerForm);
		hideElement(welcomeMsg);
		
		clearLoginError();
		showElement(loginForm);
	}

	function hideElement(element) {
		element.style.display = 'none';
	}

	function showElement(element, style) {
		var displayStyle = style ? style : 'block';
		element.style.display = displayStyle;
	}

	function showRegisterForm() {
		var loginForm = document.querySelector('#login-form');
		var welcomeMsg = document.querySelector('#welcome-msg');
		var registerForm = document.querySelector('#register-form');
		var logoutBtn = document.querySelector('#logout-link');
//		logout();
		hideElement(logoutBtn);
		hideElement(welcomeMsg);
		hideElement(loginForm);
		clearRegisterResult();
		showElement(registerForm);
	}
	function logout() {
		// The request parameters
		var url = './logout';
		var req = JSON.stringify({});

		ajax('GET', url, req,
		// successful callback
		function(res) {
			var result = JSON.parse(res);
		},
		// error
		function() {
			showLoginError('退出登陆失败！');
		}, true);
	}
	
	
	// -----------------------------------
	// Register
	// -----------------------------------

	function register() {
		var username = document.querySelector('#register-username').value;
		var password1 = document.querySelector('#register-password1').value;
		var password2 = document.querySelector('#register-password2').value;

		if (username === "" || password1 === "" || password2 === "") {
			showRegisterResult('请将用户名密码填写完整！');
			return
		}
		
		if (username.length < 6 || username.length > 18) {
            showRegisterResult("用户名长度不符合要求，请重新输入！")
            return
        }
		

		if (username.match(/^[a-zA-Z][a-zA-Z0-9_]*$/) === null) {
			showRegisterResult('用户名不符合规范，请重新输入！');
			return

		}
		
		if(password1.length < 8 || password1.length > 18) {
            showRegisterResult("密码长度不符合要求，请重新输入！")
            return
        }
		
		if (password1 !== password2) {
			showRegisterResult('两次输入密码不一样，请重新输入！');
			return

		}
		if (password1 === username) {
			showRegisterResult('密码不能与用户名相同，请重新输入！');
			return

			

		}
		var num = 0;
		num = password1.match(/[0-9]/) !== null ? num + 1 : num;
		num = password1.match(/[a-z]/) !== null ? num + 1 : num;
		num = password1.match(/[A-Z]/) !== null ? num + 1 : num;
		num = password1.match(/[!@#$&()]/) !== null ? num + 1 : num;
		if (num < 3) {
			showRegisterResult('密码不符合规范，请重新输入！');
			return

			

		}
		password = md5(username + md5(password1));

		// The request parameters
		var url = './register';
		var req = JSON.stringify({
			user_id : username,
			password : password,
		});

		ajax('POST', url, req,
		// successful callback
		function(res) {
			var result = JSON.parse(res);

			// successfully logged in
			if (result.status === 'OK') {
				console.log()
				showRegisterResult('注册成功！');
			} else {
				showRegisterResult('用户名已存在，请重新输入！');
			}
		},

		// error
		function() {
			showRegisterResult('注册失败，请重新注册！');
		}, true);
	}

	function showRegisterResult(registerMessage) {
		document.querySelector('#register-result').innerHTML = registerMessage;
	}

	function clearRegisterResult() {
		document.querySelector('#register-result').innerHTML = '';
	}

	// -----------------------------------
	// check login
	// -----------------------------------
	function checkLogin() {
		var username = document.querySelector('#username').value;
		var password = document.querySelector('#password').value;
		password = md5(username + md5(password));
		// The request parameters
		var url = './login';
		var req = JSON.stringify({});

		// make AJAX call
		ajax('GET', url, req,
		// session is still valid
		function(res) {
			var result = JSON.parse(res);

			if (result.status === 'OK' && result.user_id === username) {
				showLoginError("您已登陆!");
			} else {
				login();
			}
		});
	}

	// -----------------------------------
	// Login
	// -----------------------------------

	function login() {
		var username = document.querySelector('#username').value;
		var password = document.querySelector('#password').value;
		password = md5(username + md5(password))

		// The request parameters
		var url = './login';
		var req = JSON.stringify({
			user_id : username,
			password : password,
		});

		ajax('POST', url, req,
		// successful callback
		function(res) {
			var result = JSON.parse(res);

			// successfully logged in
			if (result.status === 'OK') {
				onSessionValid(result);
			} else if (result.status === "wrong_input") {
				console.log("wrong_input");
				showLoginError("用户名不存在，请重新输入！")
			} else if (result.status === "locked") {
				showLoginError("您的帐号已锁定，请" + result.wait_time + "小时后再试！");
			} else if (result.status === "wrong_password") {
				showLoginError("密码错误，您还有" + result.rest_attempt + "次机会！");
			}
		},

		// error
		function() {
			showLoginError("error");
		}, true);
	}

	function showLoginError(errorMessage) {
		document.querySelector('#login-error').innerHTML = errorMessage;
	}

	function clearLoginError() {
		document.querySelector('#login-error').innerHTML = '';
	}

	/**
	 * AJAX helper
	 * 
	 * @param method -
	 *            GET|POST|PUT|DELETE
	 * @param url -
	 *            API end point
	 * @param data -
	 *            request payload data
	 * @param successCallback -
	 *            Successful callback function
	 * @param errorCallback -
	 *            Error callback function
	 */
	function ajax(method, url, data, successCallback, errorCallback) {
		var xhr = new XMLHttpRequest();

		xhr.open(method, url, true);

		xhr.onload = function() {
			if (xhr.status !== null) {
				successCallback(xhr.responseText);
			} else {
				errorCallback();
			}
		};

		xhr.onerror = function() {
			console.error("The request couldn't be completed.");
			errorCallback();
		};

		if (data === null) {
			xhr.send();
		} else {
			xhr.setRequestHeader("Content-Type",
					"application/json;charset=utf-8");
			xhr.send(data);
		}
	}

	init();

})();