const express = require('express');
const router = express.Router();
const db = require('../db');

/**
 * Helper: Get latest carbon credit balance for employee.
 */
function getLatestBalance(employee_id, callback) {
  const query = `
    SELECT carbon_credits FROM employee_carboncredits
    WHERE employee_id = ?
    ORDER BY recorded_at DESC
    LIMIT 1
  `;

  db.query(query, [employee_id], (err, result) => {
    if (err) return callback(err);
    const latest = result.length > 0 ? result[0].carbon_credits : 0;
    callback(null, latest);
  });
}

/**
 * Utility: Get formatted datetime string (e.g. "2025-04-21 23:59:00")
 */
function getCurrentDateTime() {
  return new Date().toISOString().replace('T', ' ').split('.')[0];
}

/**
 * Endpoint to Buy carbon credits.
 */
router.post('/buy-credits', (req, res) => {
  const { employee_id, amount } = req.body;
  const recorded_at = getCurrentDateTime();

  if (!employee_id || !amount) {
    return res.status(400).json({ message: 'Missing employee_id or amount' });
  }

  getLatestBalance(employee_id, (err, latest) => {
    if (err) return res.status(500).json({ message: 'Error fetching latest balance' });

    const newBalance = latest + amount;

    const insertTransaction = `
      INSERT INTO transactions (employee_id, amount, type, transaction_date)
      VALUES (?, ?, 'buy', ?)
    `;

    const insertCredits = `
      INSERT INTO employee_carboncredits (employee_id, carbon_credits, recorded_at)
      VALUES (?, ?, ?)
    `;

    db.query(insertTransaction, [employee_id, amount, recorded_at], (err) => {
      if (err) return res.status(500).json({ message: 'Error inserting transaction' });

      db.query(insertCredits, [employee_id, newBalance, recorded_at], (err2) => {
        if (err2) return res.status(500).json({ message: 'Error inserting credits' });
        res.status(201).json({ message: 'Buy transaction recorded' });
      });
    });
  });
});

/**
 * Endpoint to Sell carbon credits.
 */
router.post('/sell-credits', (req, res) => {
  const { employee_id, amount } = req.body;
  const recorded_at = getCurrentDateTime();

  if (!employee_id || !amount) {
    return res.status(400).json({ message: 'Missing employee_id or amount' });
  }

  getLatestBalance(employee_id, (err, latest) => {
    if (err) return res.status(500).json({ message: 'Error fetching latest balance' });

    const newBalance = latest - amount;

    if (newBalance < 0) {
      return res.status(400).json({ message: 'Not enough credits to sell' });
    }

    const insertTransaction = `
      INSERT INTO transactions (employee_id, amount, type, transaction_date)
      VALUES (?, ?, 'sell', ?)
    `;

    const insertCredits = `
      INSERT INTO employee_carboncredits (employee_id, carbon_credits, recorded_at)
      VALUES (?, ?, ?)
    `;

    db.query(insertTransaction, [employee_id, amount, recorded_at], (err) => {
      if (err) return res.status(500).json({ message: 'Error inserting transaction' });

      db.query(insertCredits, [employee_id, newBalance, recorded_at], (err2) => {
        if (err2) return res.status(500).json({ message: 'Error inserting credits' });
        res.status(201).json({ message: 'Sell transaction recorded' });
      });
    });
  });
});

/**
 * Endpoint: Record a completed trip and assign credits.
 */
router.post('/add-trip', (req, res) => {
  const { employee_id, vehicle_type, miles } = req.body;
  const recorded_at = getCurrentDateTime();

  if (!employee_id || !vehicle_type || !miles) {
    return res.status(400).json({ message: 'Missing employee_id, vehicle_type, or miles' });
  }

  const multipliers = {
    car: 1,
    rideshare: 1.5,
    bike: 2,
    public_transport: 3
  };

  const multiplier = multipliers[vehicle_type];
  if (!multiplier) {
    return res.status(400).json({ message: 'Invalid vehicle_type' });
  }

  const creditsEarned = Math.round(miles * multiplier);

  const insertMiles = `
    INSERT INTO employee_miles (employee_id, vehicle_type, miles, recorded_at)
    VALUES (?, ?, ?, ?)
  `;

  db.query(insertMiles, [employee_id, vehicle_type, miles, recorded_at], (err) => {
    if (err) return res.status(500).json({ message: 'Error inserting miles record' });

    getLatestBalance(employee_id, (err2, latestBalance) => {
      if (err2) return res.status(500).json({ message: 'Error fetching latest balance' });

      const newBalance = latestBalance + creditsEarned;

      const insertCredits = `
        INSERT INTO employee_carboncredits (employee_id, carbon_credits, recorded_at)
        VALUES (?, ?, ?)
      `;

      db.query(insertCredits, [employee_id, newBalance, recorded_at], (err3) => {
        if (err3) return res.status(500).json({ message: 'Error inserting new credit balance' });

        return res.status(201).json({
          message: 'Trip and carbon credits recorded successfully',
          credits_earned: creditsEarned,
          new_balance: newBalance
        });
      });
    });
  });
});

/**
 * Get all mileage records for an employee with calculated carbon credits
 */
router.get('/employee-miles/:employee_id', (req, res) => {
  const { employee_id } = req.params;

  const multipliers = {
    car: 1,
    rideshare: 1.5,
    bike: 2,
    public_transport: 3
  };

  const query = `
    SELECT vehicle_type, miles, recorded_at
    FROM employee_miles
    WHERE employee_id = ?
    ORDER BY recorded_at DESC
  `;

  db.query(query, [employee_id], (err, results) => {
    if (err) return res.status(500).json({ message: 'Database error', error: err });

    const data = results.map(record => {
      const multiplier = multipliers[record.vehicle_type] || 1;
      const carbon_credits = Math.round(record.miles * multiplier);

      return {
        vehicle_type: record.vehicle_type,
        miles: record.miles,
        carbon_credits,
        recorded_at: record.recorded_at
      };
    });

    return res.status(200).json({
      message: `Trip data for employee ${employee_id}`,
      data
    });
  });
});

/**
 * Get carbon credit transaction history for an employee
 */
router.get('/employee-transactions/:employee_id', (req, res) => {
  const { employee_id } = req.params;

  const query = `
    SELECT type, amount, transaction_date
    FROM transactions
    WHERE employee_id = ?
    ORDER BY transaction_date DESC
  `;

  db.query(query, [employee_id], (err, results) => {
    if (err) {
      return res.status(500).json({ message: 'Database error', error: err });
    }

    return res.status(200).json({
      message: `Transaction history for employee ${employee_id}`,
      data: results
    });
  });
});

/**
 * Get latest carbon credit balance for an employee
 */
router.get('/latest-credits/:employee_id', (req, res) => {
  const { employee_id } = req.params;

  const query = `
    SELECT carbon_credits, recorded_at FROM employee_carboncredits
    WHERE employee_id = ?
    ORDER BY recorded_at DESC
    LIMIT 1
  `;

  db.query(query, [employee_id], (err, results) => {
    if (err) return res.status(500).json({ message: 'Database error' });

    if (results.length === 0) {
      return res.status(200).json({ message: 'No carbon credits yet', carbon_credits: 0 });
    }

    return res.status(200).json({
      message: 'Latest carbon credits fetched successfully',
      carbon_credits: results[0].carbon_credits,
      recorded_at: results[0].recorded_at
    });
  });
});

/**
 * Get latest miles record for an employee
 */
router.get('/latest-miles/:employee_id', (req, res) => {
  const { employee_id } = req.params;

  const query = `
    SELECT vehicle_type, miles, recorded_at
    FROM employee_miles
    WHERE employee_id = ?
    ORDER BY recorded_at DESC
    LIMIT 1
  `;

  db.query(query, [employee_id], (err, results) => {
    if (err) return res.status(500).json({ message: 'Database error', error: err });

    if (results.length === 0) {
      return res.status(200).json({ message: 'No mileage records found', data: null });
    }

    return res.status(200).json({
      message: 'Latest mileage entry fetched successfully',
      data: results[0]
    });
  });
});

module.exports = router;
