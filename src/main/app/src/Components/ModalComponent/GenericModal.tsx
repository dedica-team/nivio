import React, { useContext } from 'react';
import Modal from "react-modal";
import ModalContext from '../../Context/Modal.context';

const GenericModal: React.FC = () => {

    Modal.setAppElement("#root");

    const modalContext = useContext(ModalContext);

    return <Modal isOpen={modalContext.modalContent !== null}
                  className="Modal"
                  overlayClassName="Overlay"
                  shouldCloseOnEsc={true}
                  contentLabel="Modal">{modalContext.modalContent}</Modal>
}

export default GenericModal;