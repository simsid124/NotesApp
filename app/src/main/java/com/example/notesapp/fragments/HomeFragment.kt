package com.example.notesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.MainActivity
import com.example.notesapp.R
import com.example.notesapp.adapter.NoteAdapter
import com.example.notesapp.databinding.FragmentHomeBinding
import com.example.notesapp.modal.Note
import com.example.notesapp.viewmodel.NoteViewModel

class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener {

    private var _binding : FragmentHomeBinding ?= null
    private val binding get() = _binding!!
    private lateinit var notesAdapter: NoteAdapter
    private lateinit var notesViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel

        setUpRecyclerView()

        binding.fabAddNote.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }
    }

    private fun setUpRecyclerView() {
        //staggered grid
        notesAdapter = NoteAdapter()

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = notesAdapter
        }

        activity?.let {
            notesViewModel.getAllNote().observe(viewLifecycleOwner) {
                note -> notesAdapter.differ.submitList(note)
                updateUI(note)
            }
        }
    }

    private fun updateUI(note: List<Note>?){
        if (note != null) {
            if (note.isNotEmpty()){
                binding.cardView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            } else{
                binding.cardView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }
    }

    //search in kotlin app
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)

        val menuSearch = menu.findItem(R.id.menu_search).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        //searchNote(query)
        return false
    }
    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText != null){
            searchNote(newText)
        }
        return true
    }

    private fun searchNote(query: String?) {
        val searchQuery = "%$query"
        notesViewModel.searchNote(searchQuery).observe(this) {
            list -> notesAdapter.differ.submitList(list)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}