import React, { useContext, ReactElement } from 'react';
import Modal from "react-modal";

interface IGenericModalProps{
    modalContent: string | ReactElement | ReactElement[] | null,
}

const GenericModal: React.FC<IGenericModalProps> = ({modalContent}) => {

    Modal.setAppElement("#root");


    return <Modal isOpen={modalContent !== null}
                  className="Modal"
                  overlayClassName="Overlay"
                  shouldCloseOnEsc={true}
                  contentLabel="Modal">{modalContent}</Modal>
}

export default GenericModal;