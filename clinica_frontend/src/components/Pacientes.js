import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Typography,
  Alert,
  Snackbar,
} from '@mui/material';
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { pacientesService } from '../services/api';
import { format } from 'date-fns';

const formatDisplayDate = (dateString) => {
  try {
    if (!dateString) return '';
    // Crear la fecha usando UTC para evitar ajustes de zona horaria
    const [year, month, day] = dateString.split('-');
    return `${day}/${month}/${year}`;
  } catch (error) {
    console.error('Error formatting display date:', error, 'for date:', dateString);
    return '';
  }
};

function Pacientes() {
  const [pacientes, setPacientes] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedPaciente, setSelectedPaciente] = useState(null);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    fecha_nacimiento: format(new Date(), 'yyyy-MM-dd'),
    email: '',
  });

  const loadPacientes = async () => {
    try {
      const response = await pacientesService.getAll();
      console.log('Pacientes cargados (raw):', response.data);
      const pacientesFormateados = Array.isArray(response.data) ? response.data.map(paciente => ({
        ...paciente,
        fecha_nacimiento: paciente.fechaNacimiento || paciente.fecha_nacimiento
      })) : [];
      console.log('Pacientes formateados:', pacientesFormateados);
      setPacientes(pacientesFormateados);
    } catch (error) {
      console.error('Error al cargar pacientes:', error);
      showSnackbar('Error al cargar los pacientes', 'error');
    }
  };

  useEffect(() => {
    loadPacientes();
  }, [loadPacientes]);

  const handleOpen = (paciente = null) => {
    if (paciente) {
      setSelectedPaciente(paciente);
      setFormData({
        nombre: paciente.nombre || '',
        apellido: paciente.apellido || '',
        fecha_nacimiento: paciente.fecha_nacimiento || '2002-02-10',
        email: paciente.email || '',
      });
    } else {
      setSelectedPaciente(null);
      setFormData({
        nombre: '',
        apellido: '',
        fecha_nacimiento: '2002-02-10',
        email: '',
      });
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedPaciente(null);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const showSnackbar = (message, severity = 'success') => {
    setSnackbar({ open: true, message, severity });
  };

  const handleSnackbarClose = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (!formData.fecha_nacimiento) {
        showSnackbar('La fecha de nacimiento es requerida', 'error');
        return;
      }

      // Validar que la fecha de nacimiento esté dentro del rango permitido
      const fechaNacimiento = new Date(formData.fecha_nacimiento);
      const fechaMinima = new Date('1995-01-01');
      const fechaMaxima = new Date();
      
      if (fechaNacimiento < fechaMinima || fechaNacimiento > fechaMaxima) {
        showSnackbar('La fecha de nacimiento debe ser desde el 1 de enero de 1995 hasta la fecha actual', 'error');
        return;
      }

      // Validar que todos los campos requeridos estén presentes y no estén vacíos
      const pacienteData = {
        ...(selectedPaciente?.id && { id: selectedPaciente.id }),
        nombre: formData.nombre.trim(),
        apellido: formData.apellido.trim(),
        fecha_nacimiento: formData.fecha_nacimiento,
        email: formData.email.trim(),
      };

      // Validar que todos los campos requeridos estén presentes
      if (!pacienteData.nombre || !pacienteData.apellido || !pacienteData.email) {
        showSnackbar('Todos los campos son requeridos', 'error');
        return;
      }

      // Validar formato de email
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(pacienteData.email)) {
        showSnackbar('El formato del email no es válido', 'error');
        return;
      }

      console.log('Enviando datos del paciente:', pacienteData);

      if (selectedPaciente) {
        await pacientesService.update(pacienteData);
        showSnackbar('Paciente actualizado correctamente');
      } else {
        await pacientesService.create(pacienteData);
        showSnackbar('Paciente creado correctamente');
      }
      handleClose();
      loadPacientes();
    } catch (error) {
      console.error('Error al guardar paciente:', error);
      // Extraer el mensaje de error del backend
      const errorMessage = error.response?.data?.message || error.response?.data || 'Error al guardar el paciente';
      showSnackbar(errorMessage, 'error');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este paciente?')) {
      try {
        await pacientesService.delete(id);
        showSnackbar('Paciente eliminado correctamente');
        loadPacientes();
      } catch (error) {
        console.error('Error al eliminar paciente porque tiene citas agendadas:', error);
        showSnackbar('Error al eliminar el paciente porque tiene citas agendadas', 'error');
      }
    }
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1" gutterBottom>
          Pacientes
        </Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Nuevo Paciente
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nombre</TableCell>
              <TableCell>Apellido</TableCell>
              <TableCell>Fecha de Nacimiento</TableCell>
              <TableCell>Email</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {pacientes.map((paciente) => (
              <TableRow key={paciente.id}>
                <TableCell>{paciente.nombre}</TableCell>
                <TableCell>{paciente.apellido}</TableCell>
                <TableCell>
                  {formatDisplayDate(paciente.fecha_nacimiento)}
                </TableCell>
                <TableCell>{paciente.email}</TableCell>
                <TableCell align="right">
                  <IconButton color="primary" onClick={() => handleOpen(paciente)}>
                    <EditIcon />
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDelete(paciente.id)}>
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
        <DialogTitle>
          {selectedPaciente ? 'Editar Paciente' : 'Nuevo Paciente'}
        </DialogTitle>
        <DialogContent>
          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
            <TextField
              fullWidth
              margin="normal"
              label="Nombre"
              name="nombre"
              value={formData.nombre}
              onChange={handleInputChange}
              required
            />
            <TextField
              fullWidth
              margin="normal"
              label="Apellido"
              name="apellido"
              value={formData.apellido}
              onChange={handleInputChange}
              required
            />
            <TextField
              fullWidth
              margin="normal"
              label="Fecha de Nacimiento"
              name="fecha_nacimiento"
              type="date"
              value={formData.fecha_nacimiento}
              onChange={handleInputChange}
              InputLabelProps={{ shrink: true }}
              required
              helperText="Fecha permitida: desde 1 de enero de 1995 hasta hoy"
              inputProps={{
                min: '1995-01-01',
                max: format(new Date(), 'yyyy-MM-dd')
              }}
            />
            <TextField
              fullWidth
              margin="normal"
              label="Email"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleInputChange}
              required
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancelar</Button>
          <Button onClick={handleSubmit} variant="contained" color="primary">
            {selectedPaciente ? 'Actualizar' : 'Crear'}
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert onClose={handleSnackbarClose} severity={snackbar.severity}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}

export default Pacientes; 