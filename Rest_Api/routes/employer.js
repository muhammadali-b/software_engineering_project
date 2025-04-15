// routes/employer.js

const express = require('express');
const router = express.Router();
const db = require('../db');

// GET all unapproved employers
router.get('/unapproved-employers', (req, res) => {
  const sql = "SELECT * FROM employers WHERE status = 'pending'";
  db.query(sql, (err, results) => {
    if (err) return res.status(500).json({ message: "Database error" });
    res.json(results);
  });
});

// Approve employer by ID
router.patch('/approve-employer/:id', (req, res) => {
    const { id } = req.params;
    const sql = "UPDATE employers SET status = 'approved' WHERE id = ?";
    db.query(sql, [id], (err, result) => {
      if (err) return res.status(500).json({ message: "Database error" });
      res.json({ message: "Employer approved successfully" });
    });
  });
  
  module.exports = router;