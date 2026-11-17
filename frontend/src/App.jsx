import { useState } from 'react'
import './App.css'

const API_URL = import.meta.env.VITE_API_URL;

function App() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [token, setToken] = useState("");
  const [loggedInUser, setLoggedInUser] = useState("");

  const [loading, setLoading] = useState("");
  const [result, setResult] = useState("");

  const [tweetContent, setTweetContent] = useState("");

  const sendRequest = async (endpoint, body) => {
    const response = await fetch(`${API_URL}${endpoint}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(
        data?.message || `Request failed: ${response.status}`
      );
    }

    return data;
  };

  const handleRegister = async () => {
    setLoading(true);
    setResult("");

    try {
      const data = await sendRequest("/register", {
        username,
        email,
        password,
      });

      setResult(
        `Registration successful!\n${JSON.stringify(data, null, 2)}`
      );
    } catch (error) {
      setResult(`Registration failed: ${error.message}`);
    } finally {
      setLoading(false);
    };
  }

  const handleLogin = async () => {
    setLoading(true);
    setResult("");

    try {
      const data = await sendRequest("/login", {
        username,
        password,
      });

      setToken(data.token);
      setLoggedInUser(data.username);
      setResult(`Login successful! Welcome, ${data.username}.`);
    } catch (error) {
      setToken("");
      setLoggedInUser("");
      setResult(`Login failed: ${error.message}`);
    } finally {
      setLoading(false);
    }
  }

  const handleLogout = () => {
    setToken("");
    setLoggedInUser("");
    setResult("Logged out successfully.")
  }

  const handleCreateTweet = async () => {
    if (!token || !tweetContent.trim()) {
      setResult("Please log in and enter tweet content.");
      return;
    }

    setLoading(true);
    setResult("");

    try {
      const response = await fetch(`${API_URL}/tweet`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          content: tweetContent,
        }),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(
          data?.message || `Request failed: ${response.status}`
        );
      }

      setResult(
        `Tweet created successfully!\n${JSON.stringify(data, null, 2)}`
      );

      setTweetContent("");
    } catch (error) {
      setResult(`Tweet creation failed: ${error.message}`);
    } finally {
      setLoading(false);
    }
  }


  return (
    <>
      <main className="app-container">
        <h1>Twitter API - React Integration</h1>
        <p className="subtitle">
          Spring Boot Backend: {API_URL}
        </p>

        <section className="card">
          <h2>Authentication</h2>

          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter username" />
          </div>
          <div className="form-group">
            <label htmlFor="email">Email (Register only)</label>
            <input id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Enter email" />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password" />
          </div>

          <div className="button-group">
            <button onClick={handleRegister} disabled={loading}>
              Register
            </button>
            <button onClick={handleLogin} disabled={loading}>
              Login
            </button>

            {token && (
              <button className="secondary-button" onClick={handleLogout}>
                Logout
              </button>
            )}
          </div>
        </section>

        <section className="card">
          <h2>Authentication Status</h2>

          <p>
            {token
              ? `Authenticated as ${loggedInUser}`
              : "Not authenticated"}
          </p>

          <p className="status-note">
            JWT is stored temporarily in React state.
          </p>
        </section>

        <section className="card">
          <h2>Create Tweet</h2>

          <p className="status-note">
            Maximum 280 characters.
          </p>

          <div className="form-group">
            <label htmlFor="tweetContent">Tweet Content</label>
            <textarea
              id="tweetContent"
              value={tweetContent}
              onChange={(e) => setTweetContent(e.target.value)}
              maxLength={280}
              placeholder="What's happening?"
              rows={5}
            />
          </div>

          <div className="tweet-footer">
              <span className="character-count">
                {tweetContent.length} / 280
              </span>

              <button
                onClick={handleCreateTweet}
                disabled={!token || !tweetContent.trim() || loading}
              >
                Post Tweet
              </button>
          </div>

          {!token && (
            <p className="status-note">
              You must log in before posting a tweet.
            </p>
          )}
        </section>

        <section className="card">
          <h2>API Response</h2>
          <pre className="response-box">
            {result || "No request has been sent yet."}
          </pre>
        </section>
      </main>
    </>
  )
}

export default App
