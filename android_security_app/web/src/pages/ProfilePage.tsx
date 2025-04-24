import React, { useState } from 'react';
import { 
  Container, 
  Box, 
  Typography, 
  TextField, 
  Button, 
  Paper, 
  Alert,
  CircularProgress,
  Divider,
  Grid,
  Card,
  CardContent
} from '@mui/material';
import { useAuth } from '../contexts/AuthContext';
import Header from '../components/Header';

const ProfilePage: React.FC = () => {
  const { user, updateProfile, changePassword, logout } = useAuth();
  
  const [name, setName] = useState(user?.name || '');
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  
  const [profileLoading, setProfileLoading] = useState(false);
  const [passwordLoading, setPasswordLoading] = useState(false);
  const [profileError, setProfileError] = useState('');
  const [profileSuccess, setProfileSuccess] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [passwordSuccess, setPasswordSuccess] = useState('');

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!name) {
      setProfileError('O nome não pode estar vazio');
      return;
    }
    
    setProfileError('');
    setProfileSuccess('');
    setProfileLoading(true);
    
    try {
      const success = await updateProfile(name);
      
      if (success) {
        setProfileSuccess('Perfil atualizado com sucesso!');
      } else {
        setProfileError('Não foi possível atualizar o perfil. Tente novamente.');
      }
    } catch (err) {
      console.error('Erro ao atualizar perfil:', err);
      setProfileError('Ocorreu um erro ao atualizar o perfil. Tente novamente.');
    } finally {
      setProfileLoading(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!currentPassword || !newPassword || !confirmPassword) {
      setPasswordError('Por favor, preencha todos os campos');
      return;
    }
    
    if (newPassword !== confirmPassword) {
      setPasswordError('As senhas não coincidem');
      return;
    }
    
    if (newPassword.length < 6) {
      setPasswordError('A nova senha deve ter pelo menos 6 caracteres');
      return;
    }
    
    setPasswordError('');
    setPasswordSuccess('');
    setPasswordLoading(true);
    
    try {
      const success = await changePassword(currentPassword, newPassword);
      
      if (success) {
        setPasswordSuccess('Senha alterada com sucesso!');
        setCurrentPassword('');
        setNewPassword('');
        setConfirmPassword('');
      } else {
        setPasswordError('Senha atual incorreta ou não foi possível alterar a senha. Tente novamente.');
      }
    } catch (err) {
      console.error('Erro ao alterar senha:', err);
      setPasswordError('Ocorreu um erro ao alterar a senha. Tente novamente.');
    } finally {
      setPasswordLoading(false);
    }
  };

  return (
    <>
      <Header title="Meu Perfil" />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Meu Perfil
        </Typography>
        
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Informações Pessoais
              </Typography>
              <Divider sx={{ mb: 3 }} />
              
              {profileError && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {profileError}
                </Alert>
              )}
              
              {profileSuccess && (
                <Alert severity="success" sx={{ mb: 2 }}>
                  {profileSuccess}
                </Alert>
              )}
              
              <Box component="form" onSubmit={handleUpdateProfile}>
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  id="name"
                  label="Nome"
                  name="name"
                  autoComplete="name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  disabled={profileLoading}
                  sx={{ mb: 2 }}
                />
                
                <TextField
                  margin="normal"
                  fullWidth
                  id="email"
                  label="Email"
                  name="email"
                  autoComplete="email"
                  value={user?.email || ''}
                  disabled={true}
                  sx={{ mb: 3 }}
                  helperText="O email não pode ser alterado"
                />
                
                <Button
                  type="submit"
                  variant="contained"
                  disabled={profileLoading}
                  sx={{ mt: 1 }}
                >
                  {profileLoading ? <CircularProgress size={24} /> : 'Atualizar Perfil'}
                </Button>
              </Box>
            </Paper>
          </Grid>
          
          <Grid item xs={12} md={6}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Alterar Senha
              </Typography>
              <Divider sx={{ mb: 3 }} />
              
              {passwordError && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {passwordError}
                </Alert>
              )}
              
              {passwordSuccess && (
                <Alert severity="success" sx={{ mb: 2 }}>
                  {passwordSuccess}
                </Alert>
              )}
              
              <Box component="form" onSubmit={handleChangePassword}>
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  name="currentPassword"
                  label="Senha Atual"
                  type="password"
                  id="currentPassword"
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  disabled={passwordLoading}
                  sx={{ mb: 2 }}
                />
                
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  name="newPassword"
                  label="Nova Senha"
                  type="password"
                  id="newPassword"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  disabled={passwordLoading}
                  sx={{ mb: 2 }}
                />
                
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  name="confirmPassword"
                  label="Confirmar Nova Senha"
                  type="password"
                  id="confirmPassword"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  disabled={passwordLoading}
                  sx={{ mb: 3 }}
                />
                
                <Button
                  type="submit"
                  variant="contained"
                  disabled={passwordLoading}
                  sx={{ mt: 1 }}
                >
                  {passwordLoading ? <CircularProgress size={24} /> : 'Alterar Senha'}
                </Button>
              </Box>
            </Paper>
          </Grid>
        </Grid>
      </Container>
    </>
  );
};

export default ProfilePage;
