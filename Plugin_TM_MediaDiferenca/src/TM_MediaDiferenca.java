// Importações necessárias
import java.awt.*;
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.*;


/**
 * @author Rafael Dantas
 */
public class TM_MediaDiferenca implements PlugIn {

    @Override
    public void run(String arg) {

        // Obtém a imagem original e suas informações / Este trecho é bastante "auto interpretável"
        ImagePlus original_imp = WindowManager.getCurrentImage();
        ImageProcessor original_ip = original_imp.getProcessor();
        int original_h = original_ip.getHeight();
        int original_w = original_ip.getWidth();

        // Se a imagem não estiver em escala de cinza de 8 bits, converte pata tal
        if ( original_imp.getType() != ImagePlus.GRAY8 ) {
            ImageConverter imc = new ImageConverter(original_imp);  // Obtem o ImageConverter do ImagePlus
            imc.convertToGray8();                                   // Converte para escala de cinza de 8 bits
            original_imp.updateAndDraw();                           // Atualiza a imagem atual
            original_imp = WindowManager.getCurrentImage();         // Obtém novamente a nova imagem atual
            original_ip = original_imp.getProcessor();              // e o seu processor
        }

        // Obtem o (template|kernel|modelo) através do ROI (área de interesse)
        Roi roi = original_imp.getRoi();
        Rectangle roiRect = roi.getBounds(); // através do ROI eu pego os limites do que foi selecionado pelo usuário
        int roi_image_w = roiRect.width;
        int roi_image_h = roiRect.height;
        int roi_tl_x = roiRect.x;            // pego o ponto x do limite esquerdo superior da área selecionada
        int roi_tl_y = roiRect.y;            // pego o ponto y do mesmo limite acima

        // Criação de arrays unidimensionais do tipo byte (correto para imagem 8 bits) que posteriormente serão atribuidas os valores dos pixels
        byte[] original_matrix = new byte[ original_w * original_h ];       // matriz da imagem original
        byte[] template_matrix = new byte[ roi_image_w * roi_image_h ];     // matriz do template, baseado no tamanho da área de interesse (ROI)
        byte[] result_matrix = new byte[ original_w * original_h ];         // matriz da imagem resultado, baseado no tamanho da imagem original

        // Construção pixels original
        for ( int h=0; h<original_h; h++)
            for ( int w=0; w<original_w; w++ )
                original_matrix[h * original_w + w] = (byte) original_ip.getPixel(w,h); // obtem o pixel, faz um cast para byte e joga na posição unidimensional relativa

        // Construção pixels template
        for ( int h=0; h<roi_image_h; h++ )
            for ( int w=0; w<roi_image_w; w++ )
                template_matrix[h * roi_image_w + w] = (byte) original_ip.getPixel(w + roi_tl_x, h + roi_tl_y); // obtem o pixel, faz um cast para byte e joga na posição unidimensional relativa

        // Algoritmo media da diferença
        int lc = roi_image_h/2;
        int cc = roi_image_w/2;
        for ( int l=0; l<original_h; l++ ) {
            for ( int c=0; c<original_w; c++ ) {
                int soma = 0;
                for ( int l2=0; l2<roi_image_h; l2++ )
                    for ( int c2=0; c2<roi_image_w; c2++ )
                        soma = soma + Math.abs( original_ip.getPixel(c+c2-cc, l+l2-lc) - original_ip.getPixel(c2 + roi_tl_x, l2 + roi_tl_y) );
                result_matrix[l * original_w + c] = (byte) ( soma / ( (roi_image_w * roi_image_h) / 2 ));
                    

            }
        }
        original_ip.setPixels(result_matrix);   // escrevo o array de pixel na imagem original

        //original_ip.autoThreshold();            // binarização da imagem
        original_imp.updateAndDraw();           // atualização da nova imagem (resultado)
        

    }
    
}
