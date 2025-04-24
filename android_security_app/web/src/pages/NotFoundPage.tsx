import React from 'react';
import { Container, Box, Typography, Button, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const NotFoundPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <Container component="main" maxWidth="md">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Paper elevation={3} sx={{ p: 4, width: '100%', borderRadius: 2, textAlign: 'center' }}>
          <Typography component="h1" variant="h3" sx={{ mb: 2 }}>
            404
          </Typography>
          <Typography component="h2" variant="h5" sx={{ mb: 3 }}>
            Página não encontrada
          </Typography>
          <Typography variant="body1" sx={{ mb: 4 }}>
            A página que você está procurando não existe ou foi movida.
          </Typography>
          <Button
            variant="contained"
            onClick={() => navigate('/')}
            sx={{ py: 1.5, px: 4 }}
          >
            Voltar para a página inicial
          </Button>
        </Paper>
      </Box>
    </Container>
  );
};

export default NotFoundPage;
