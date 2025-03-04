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
import { consultoriosService } from '../services/api';

function Consultorios() {
  const [consultorios, setConsultorios] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedConsultorio, setSelectedConsultorio] = useState(null);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [formData, setFormData] = useState({
    numero: '',
    piso: '',
  });

  const loadConsultorios = async () => {
    try {
      const response = await consultoriosService.getAll();
      setConsultorios(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error('Error al cargar consultorios:', error);
      const errorMessage = error.response?.data || 
                        (typeof error.message === 'string' ? error.message : 'Error al cargar los consultorios');
      showSnackbar(errorMessage, 'error');
      setConsultorios([]);
    }
  };

  useEffect(() => {
    loadConsultorios();
  }, [loadConsultorios]);

  const handleOpen = (consultorio = null) => {
    if (consultorio) {
      setSelectedConsultorio(consultorio);
      setFormData({
        numero: consultorio.numero || '',
        piso: consultorio.piso || '',
      });
    } else {
      setSelectedConsultorio(null);
      setFormData({
        numero: '',
        piso: '',
      });
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedConsultorio(null);
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
      if (!formData.numero || formData.piso === '') {
        showSnackbar('Todos los campos son requeridos', 'error');
        return;
      }

      const consultorioData = {
        numero: formData.numero.trim(),
        piso: parseInt(formData.piso, 10)
      };

      // Validar que el piso sea un número válido
      if (isNaN(consultorioData.piso)) {
        showSnackbar('El piso debe ser un número válido', 'error');
        return;
      }

      if (selectedConsultorio) {
        await consultoriosService.update({ ...consultorioData, id: selectedConsultorio.id });
        showSnackbar('Consultorio actualizado correctamente');
      } else {
        await consultoriosService.create(consultorioData);
        showSnackbar('Consultorio creado correctamente');
      }
      handleClose();
      loadConsultorios();
    } catch (error) {
      console.error('Error al guardar consultorio:', error);
      const errorMessage = error.response?.data || 
                         (typeof error.message === 'string' ? error.message : 'Error al guardar el consultorio');
      showSnackbar(errorMessage, 'error');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este consultorio?')) {
      try {
        await consultoriosService.delete(id);
        showSnackbar('Consultorio eliminado correctamente');
        loadConsultorios();
      } catch (error) {
        console.error('Error al eliminar consultorio:', error);
        const errorMessage = error.response?.data || 
                          (typeof error.message === 'string' ? error.message : 'Error al eliminar el consultorio');
        showSnackbar(errorMessage, 'error');
      }
    }
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1" gutterBottom>
          Consultorios
        </Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Nuevo Consultorio
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Número</TableCell>
              <TableCell>Piso</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {consultorios.map((consultorio) => (
              <TableRow key={consultorio.id || Math.random()}>
                <TableCell>{consultorio.numero}</TableCell>
                <TableCell>{consultorio.piso}</TableCell>
                <TableCell align="right">
                  <IconButton 
                    color="primary" 
                    onClick={() => handleOpen(consultorio)}
                    disabled={!consultorio.id}
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton 
                    color="error" 
                    onClick={() => consultorio.id && handleDelete(consultorio.id)}
                    disabled={!consultorio.id}
                  >
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {(!consultorios || consultorios.length === 0) && (
              <TableRow>
                <TableCell colSpan={3} align="center">
                  No hay consultorios registrados
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
        <DialogTitle>
          {selectedConsultorio ? 'Editar Consultorio' : 'Nuevo Consultorio'}
        </DialogTitle>
        <DialogContent>
          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
            <TextField
              fullWidth
              margin="normal"
              label="Número"
              name="numero"
              value={formData.numero}
              onChange={handleInputChange}
              required
            />
            <TextField
              fullWidth
              margin="normal"
              label="Piso"
              name="piso"
              type="number"
              value={formData.piso}
              onChange={handleInputChange}
              required
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancelar</Button>
          <Button onClick={handleSubmit} variant="contained" color="primary">
            {selectedConsultorio ? 'Actualizar' : 'Crear'}
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

export default Consultorios; 