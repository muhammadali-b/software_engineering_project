const express = require('express');
const cors = require('cors');
const authRoutes = require('./routes/auth');
const employeeRoutes = require('./routes/employee');
const employerRoutes = require('./routes/employer');

const app = express();
app.use(cors());
app.use(express.json());

app.use('/api', authRoutes);
app.use('/api', employeeRoutes);
app.use('/api', employerRoutes);

const PORT = process.env.PORT || 5555;

app.listen(PORT, () => console.log(`🚀 Server running at http://localhost:${PORT}`));