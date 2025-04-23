// routes/employer.js

const express = require('express');
const router = express.Router();
const db = require('../db');

/**
 * GET all unapproved employers
 */
router.get('/unapproved-employers', (req, res) => {
  const sql = "SELECT * FROM employers WHERE status = 'pending'";
  db.query(sql, (err, results) => {
    if (err) return res.status(500).json({ message: "Database error" });
    res.json(results);
  });
});

/**
 * Approve an employer
 */
router.patch('/approve-employer/:id', (req, res) => {
  const employerId = req.params.id;

  const sql = "UPDATE users SET status = 'approved' WHERE id = ? AND role = 'employer'";

  db.query(sql, [employerId], (err, result) => {
    if (err) {
      return res.status(500).json({ message: 'Database error' });
    }

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: 'Employer not found or already approved' });
    }

    return res.status(200).json({ message: 'Employer approved successfully' });
  });
});

/**
 * GET all approved employers
 */
router.get('/approved-employers', (req, res) => {
  const sql = "SELECT * FROM users WHERE status = 'approved' AND role = 'employer'";
  db.query(sql, (err, results) => {
    if (err) return res.status(500).json({ message: "Database error" });
    res.json(results);
  });
});


  
  module.exports = router;