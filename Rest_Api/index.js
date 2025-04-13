const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const authRoutes = require('./routes/auth');
const employeeRoutes = require('./routes/employee');

const app = express();
app.use(cors());
app.use(bodyParser.json());

app.use('/api', authRoutes);
app.use('/api', employeeRoutes);

const PORT = process.env.PORT || 5555;

app.listen(PORT, () => console.log(`🚀 Server running at http://localhost:${PORT}`));