const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const db = require('../db');

/**
 * Register endpoint.
 */
router.post('/register', async (req, res) => {
  const { f_name, l_name, email, password, role } = req.body;

  if (!f_name || !l_name || !email || !password || !role) {
    return res.status(400).json({ message: 'All fields are required' });
  }

  db.query('SELECT * FROM users WHERE email = ?', [email], async (err, result) => {
    if (err) {
      return res.status(500).json({ message: 'Database error' });
    }

    if (result.length > 0) {
      return res.status(400).json({ message: 'Email already exists' });
    }

    try {
      const hashedPassword = await bcrypt.hash(password, 10);

      // Determine initial status based on role
      let status;
      if (role === 'bank') {
        status = 'approved';
      } else if (role === 'admin') {
        status = 'approved';
      } else if (role === 'employer' || role === 'employee') {
        status = 'pending';
      } else {
        return res.status(400).json({ message: 'Invalid role specified' });
      }

      db.query(
        'INSERT INTO users (f_name, l_name, email, password, role, status) VALUES (?, ?, ?, ?, ?, ?)',
        [f_name, l_name, email, hashedPassword, role, status],
        (err, result) => {
          if (err) {
            return res.status(500).json({ message: 'Database error on insert' });
          }

          return res.status(201).json({ message: 'User registered successfully' });
        }
      );
    } catch (err) {
      return res.status(500).json({ message: 'Password encryption error' });
    }
  });
});

/**
 * Login endpoint.
 */
router.post('/login', (req, res) => {
  const { email, password } = req.body;

  db.query('SELECT * FROM users WHERE email = ?', [email], async (err, result) => {
    if (err) {
      return res.status(500).json({ message: 'Database error' });
    }

    if (result.length === 0) {
      return res.status(401).json({ message: 'Invalid credentials' });
    }

    // Check if the user is approved
    if (result[0].status !== 'approved') {
      return res.status(403).json({ message: 'Account not approved yet. Please wait for approval.' });
    }

    const validPass = await bcrypt.compare(password, result[0].password);
    if (!validPass) {
      return res.status(401).json({ message: 'Invalid credentials' });
    }

    return res.status(200).json({
      message: 'Login successful',
      user: {
        id: result[0].id,
        first_name: result[0].f_name,
        last_name: result[0].l_name,
        email: result[0].email,
        role: result[0].role
      }
    });
  });
});

/**
 * Change password endpoint.
 */
router.post('/change-password', async (req, res) => {
  const { email, oldPassword, newPassword } = req.body;

  if (!email || !oldPassword || !newPassword) {
    return res.status(400).json({ message: 'All fields are required' });
  }

  db.query('SELECT * FROM users WHERE email = ?', [email], async (err, result) => {
    if (err) return res.status(500).json({ message: 'Database error' });

    if (result.length === 0) {
      return res.status(404).json({ message: 'User not found' });
    }

    const isMatch = await bcrypt.compare(oldPassword, result[0].password);
    if (!isMatch) {
      return res.status(401).json({ message: 'Incorrect old password' });
    }

    const hashedNewPassword = await bcrypt.hash(newPassword, 10);

    db.query(
      'UPDATE users SET password = ? WHERE email = ?',
      [hashedNewPassword, email],
      (err2) => {
        if (err2) return res.status(500).json({ message: 'Error updating password' });
        return res.status(200).json({ message: 'Password updated successfully' });
      }
    );
  });
});

module.exports = router;