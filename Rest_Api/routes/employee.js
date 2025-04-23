const express = require('express');
const router = express.Router();
const db = require('../db');  // using your existing db.js connection

/**
 * Get all unapproved employees
 */
router.get('/unapproved-employees', (req, res) => {
  const sql = "SELECT id, f_name, l_name, email FROM users WHERE role = 'employee' AND status = 'pending'";

  db.query(sql, (err, result) => {
    if (err) {
      return res.status(500).json({ message: 'Database error' });
    }

    return res.status(200).json({
      employees: result
    });
  });
});

/**
 * Approve an employee
 */
router.patch('/approve-employee/:id', (req, res) => {
  const employeeId = req.params.id;

  const sql = "UPDATE users SET status = 'approved' WHERE id = ? AND role = 'employee'";

  db.query(sql, [employeeId], (err, result) => {
    if (err) {
      return res.status(500).json({ message: 'Database error' });
    }

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: 'Employee not found or already approved' });
    }

    return res.status(200).json({ message: 'Employee approved successfully' });
  });
});

/**
 * Get all approved employees
 */
router.get('/approved-employees', (req, res) => {
  const query = `
    SELECT id, f_name, l_name, email
    FROM users
    WHERE role = 'employee' AND status = 'approved'
  `;

  db.query(query, (err, results) => {
    if (err) {
      return res.status(500).json({ message: 'Database error' });
    }

    res.status(200).json({
      message: 'Approved employees fetched successfully',
      data: results
    });
  });
});


module.exports = router;