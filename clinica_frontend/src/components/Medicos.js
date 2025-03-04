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
import { medicosService } from '../services/api';

function Medicos() {
  const [medicos, setMedicos] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedMedico, setSelectedMedico] = useState(null);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    especialidad: '',
  });

  const loadMedicos = async () => {
    try {
      const response = await medicosService.getAll();
      setMedicos(response.data || []);
    } catch (error) {
      console.error('Error al cargar médicos:', error);
      showSnackbar('Error al cargar los médicos', 'error');
    }
  };

  useEffect(() => {
    loadMedicos();
  }, [loadMedicos]);

  const handleOpen = (medico = null) => {
    if (medico) {
      setSelectedMedico(medico);
      setFormData({
        nombre: medico.nombre || '',
        apellido: medico.apellido || '',
        especialidad: medico.especialidad || '',
      });
    } else {
      setSelectedMedico(null);
      setFormData({
        nombre: '',
        apellido: '',
        especialidad: '',
      });
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedMedico(null);
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
      // Validar que los campos requeridos no estén vacíos
      if (!formData.nombre || !formData.apellido || !formData.especialidad) {
        showSnackbar('Todos los campos son requeridos', 'error');
        return;
      }

      if (selectedMedico) {
        await medicosService.update(selectedMedico.id, formData);
        showSnackbar('Médico actualizado correctamente');
      } else {
        await medicosService.create(formData);
        showSnackbar('Médico creado correctamente');
      }
      handleClose();
      loadMedicos();
    } catch (error) {
      console.error('Error al guardar médico:', error);
      const errorMessage = error.response?.data || error.message || 'Error al guardar el médico';
      showSnackbar(errorMessage, 'error');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este médico?')) {
      try {
        await medicosService.delete(id);
        showSnackbar('Médico eliminado correctamente');
        loadMedicos();
      } catch (error) {
        console.error('Error al eliminar médico porque tiene citas agendadas:', error);
        showSnackbar('Error al eliminar el médico porque tiene citas agendadas', 'error');
      }
    }
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1" gutterBottom>
          Médicos
        </Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Nuevo Médico
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nombre</TableCell>
              <TableCell>Apellido</TableCell>
              <TableCell>Especialidad</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {medicos.map((medico) => (
              <TableRow key={medico.id}>
                <TableCell>{medico.nombre}</TableCell>
                <TableCell>{medico.apellido}</TableCell>
                <TableCell>{medico.especialidad}</TableCell>
                <TableCell align="right">
                  <IconButton color="primary" onClick={() => handleOpen(medico)}>
                    <EditIcon />
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDelete(medico.id)}>
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
          {selectedMedico ? 'Editar Médico' : 'Nuevo Médico'}
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
              label="Especialidad"
              name="especialidad"
              value={formData.especialidad}
              onChange={handleInputChange}
              required
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancelar</Button>
          <Button onClick={handleSubmit} variant="contained" color="primary">
            {selectedMedico ? 'Actualizar' : 'Crear'}
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

export default Medicos; 