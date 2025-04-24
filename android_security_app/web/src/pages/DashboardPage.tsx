import React, { useState, useEffect } from 'react';
import { 
  Container, 
  Box, 
  Typography, 
  Paper, 
  Grid,
  Button,
  CircularProgress,
  Divider,
  Card,
  CardContent,
  CardActions,
  IconButton,
  Tooltip,
  Alert
} from '@mui/material';
import { 
  Refresh as RefreshIcon,
  LocationOn as LocationIcon,
  Lock as LockIcon,
  VolumeUp as VolumeUpIcon,
  VolumeOff as VolumeOffIcon,
  Phone as PhoneIcon,
  Camera as CameraIcon,
  Settings as SettingsIcon,
  Delete as DeleteIcon
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { getDevices, Device } from '../services/deviceService';
import Header from '../components/Header';

const DashboardPage: React.FC = () => {
  const [devices, setDevices] = useState<Device[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadDevices();
  }, []);

  const loadDevices = async () => {
    setLoading(true);
    setError('');
    
    try {
      const devicesList = await getDevices();
      setDevices(devicesList);
    } catch (err) {
      console.error('Erro ao carregar dispositivos:', err);
      setError('Não foi possível carregar seus dispositivos. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  const formatLastSeen = (timestamp: number) => {
    const date = new Date(timestamp);
    return date.toLocaleString('pt-BR');
  };

  const handleDeviceClick = (deviceId: string) => {
    navigate(`/devices/${deviceId}`);
  };

  return (
    <>
      <Header title="Dashboard" />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h4" component="h1">
            Meus Dispositivos
          </Typography>
          <Button 
            variant="contained" 
            startIcon={<RefreshIcon />}
            onClick={loadDevices}
            disabled={loading}
          >
            Atualizar
          </Button>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 5 }}>
            <CircularProgress />
          </Box>
        ) : devices.length === 0 ? (
          <Paper sx={{ p: 4, textAlign: 'center' }}>
            <Typography variant="h6" gutterBottom>
              Nenhum dispositivo encontrado
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
              Você ainda não tem dispositivos registrados no sistema.
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Para registrar um dispositivo, instale o aplicativo SecureGuard em seu celular Android e faça login com sua conta.
            </Typography>
          </Paper>
        ) : (
          <Grid container spacing={3}>
            {devices.map((device) => (
              <Grid item xs={12} md={6} lg={4} key={device.id}>
                <Card 
                  sx={{ 
                    height: '100%', 
                    display: 'flex', 
                    flexDirection: 'column',
                    cursor: 'pointer',
                    transition: 'transform 0.2s',
                    '&:hover': {
                      transform: 'translateY(-4px)',
                      boxShadow: '0 8px 16px rgba(0,0,0,0.1)'
                    }
                  }}
                  onClick={() => handleDeviceClick(device.id)}
                >
                  <CardContent sx={{ flexGrow: 1 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                      <Typography variant="h6" component="h2">
                        {device.name}
                      </Typography>
                      <Box 
                        sx={{ 
                          width: 12, 
                          height: 12, 
                          borderRadius: '50%', 
                          bgcolor: device.isActive ? 'success.main' : 'error.main' 
                        }} 
                      />
                    </Box>
                    
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      ID: {device.deviceId.substring(0, 8)}...
                    </Typography>
                    
                    <Typography variant="body2" color="text.secondary">
                      Última atividade: {formatLastSeen(device.lastSeen)}
                    </Typography>
                    
                    <Divider sx={{ my: 2 }} />
                    
                    <Typography variant="body2" gutterBottom>
                      Funcionalidades ativas:
                    </Typography>
                    
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mt: 1 }}>
                      {device.settings.locationTrackingEnabled && (
                        <Tooltip title="Rastreamento de localização">
                          <IconButton size="small" color="primary">
                            <LocationIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                      
                      {device.settings.remoteLockEnabled && (
                        <Tooltip title="Bloqueio remoto">
                          <IconButton size="small" color="primary">
                            <LockIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                      
                      {device.settings.soundAlertEnabled && (
                        <Tooltip title="Alerta sonoro">
                          <IconButton size="small" color="primary">
                            <VolumeUpIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                      
                      {device.settings.callControlEnabled && (
                        <Tooltip title="Controle de ligações">
                          <IconButton size="small" color="primary">
                            <PhoneIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                      
                      {device.settings.wrongPasswordPhotoEnabled && (
                        <Tooltip title="Foto em senha incorreta">
                          <IconButton size="small" color="primary">
                            <CameraIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                    </Box>
                  </CardContent>
                  
                  <CardActions sx={{ justifyContent: 'flex-end', p: 2, pt: 0 }}>
                    <Tooltip title="Configurações">
                      <IconButton 
                        size="small"
                        onClick={(e) => {
                          e.stopPropagation();
                          navigate(`/devices/${device.id}/settings`);
                        }}
                      >
                        <SettingsIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  </CardActions>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </Container>
    </>
  );
};

export default DashboardPage;
